package com.example.myapplication.ViewModels

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.ApiRecipeD
import com.example.myapplication.model.Comment
import com.example.myapplication.model.Recipe
import com.example.myapplication.model.RecipePreview
import com.example.myapplication.repository.CloudinaryRepository
import com.example.myapplication.repository.CommentRepository
import com.example.myapplication.repository.RecipeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime

class RecipeViewModel : ViewModel() {

    private val repository = RecipeRepository()
    val cloudinaryRepository = CloudinaryRepository()
    val collectionName = "RecipeImages"

    private val _recipes = MutableLiveData<List<Recipe>?>() // רשימת מתכונים
    val recipes: MutableLiveData<List<Recipe>?> = _recipes

    private val _selectedRecipe = MutableLiveData<Recipe?>()
    val selectedRecipe: LiveData<Recipe?> get() = _selectedRecipe


    private val _selectedTags = MutableLiveData<List<String>>()
    val selectedTags: LiveData<List<String>> get() = _selectedTags

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val recipeRepository = RecipeRepository()
    private val commentRepository = CommentRepository()

    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> get() = _comments

    private val _filteredRecipes = MutableLiveData<List<RecipePreview>?>()
    val filteredRecipes: LiveData<List<RecipePreview>?> = _filteredRecipes

    fun getFileFromUri(context: Context, uri: String): File? {
        val fileUri = Uri.parse(uri)
        val cursor = context.contentResolver.query(fileUri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val fileName = it.getString(columnIndex)
                val file = File(context.cacheDir, fileName)

                context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                return file
            }
        }
        return null
    }

    private suspend fun uploadImageAndGetUrl(image: File): String? {
        return try {
            cloudinaryRepository.uploadImage(image, collectionName)?.replace("http://", "https://") // שלח את הקונטקסט כאן
        } catch (e: Exception) {
            Log.e("CloudinaryError", "Failed to upload image", e)
            null
        }
    }

    fun searchRecipes(query: String) {
        val lowerCaseQuery = query.lowercase()

        val previewRecipes = castRecipeToPreview(recipes.value ?: emptyList())

        val filteredList = previewRecipes.filter { recipe ->
            recipe.title.lowercase().contains(lowerCaseQuery) ||
                    recipe.tags.any { it.lowercase().contains(lowerCaseQuery) }
        }

        _filteredRecipes.value = filteredList
    }

    fun fetchRecipes() {
        viewModelScope.launch {
            val recipeList = repository.getAllRecipes()

            _recipes.value = recipeList
        }
    }

    fun fetchMyRecipes(uid: String) {
        viewModelScope.launch {
            val recipeList = repository.getUserRecipes(uid)
            _recipes.value = recipeList
            _filteredRecipes.value = castRecipeToPreview(recipeList)
        }
    }

    suspend fun isRecipeMine(userId: String, recipeId: String): Boolean {
        return try {
            val userRecipes = repository.getUserRecipes(userId)
            userRecipes.any { it.id == recipeId }
        } catch (e: Exception) {
            Log.e("Firebase", "Error checking recipe ownership", e)
            false
        }
    }


    fun castRecipeToPreview(recipes:List<Recipe>) : List<RecipePreview> {
        val recipePreviews = recipes.map { recipe ->
            RecipePreview(
                id = recipe.id,
                title = recipe.title,
                imageUrl = recipe.image,
                tags = recipe.tags,
                comments = listOf()
            )
        }
        return recipePreviews
    }

    fun addRecipe(context: Context, recipe: Recipe) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageFile = recipe.image?.let { getFileFromUri(context, it) }
                val imageUrl = imageFile?.let { uploadImageAndGetUrl(it) } ?: ""

                val updatedRecipe = recipe.copy(image = imageUrl)

                val success = repository.addRecipe(updatedRecipe)

                if (success) {
                    fetchRecipes()
                } else {
                    Log.e("addRecipe", "Failed to add recipe")
                }
            } catch (e: Exception) {
                Log.e("addRecipe", "Error adding recipe: ${e.message}", e)
            }
        }
    }


    fun addLike(id:String) {
        viewModelScope.launch {
            val success = repository.addLike(id)
            if (success) fetchRecipes()
        }
    }

    fun updateRecipe(context: Context, recipe: Recipe) {
        CoroutineScope(Dispatchers.IO).launch {
            val imageUrl = if (recipe.image.startsWith("http")) {
                recipe.image
            } else {
                val imageFile = getFileFromUri(context, recipe.image)
                imageFile?.let { uploadImageAndGetUrl(it) }
            }
            val updatedRecipe = imageUrl?.let { recipe.copy(image = it) } ?: recipe

            val success = repository.updateRecipe(updatedRecipe)
            if (success) fetchRecipes()
        }
    }

    fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            try {
                val recipeMap = repository.getRecipe(recipeId)
                if (recipeMap != null) {
                    val recipe = mapToRecipe(recipeMap)
                    Log.d("Recipe","recipe:"+recipe)
                    _selectedRecipe.value = recipe
                } else {
                    _errorMessage.postValue("Error: Recipe not found")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error loading recipe: ${e.message}")
            }
        }
    }

    private fun mapToRecipe(recipeMap: Map<String, Any>): Recipe {
        return Recipe(
            id = recipeMap["id"] as? String ?: "",
            title = recipeMap["title"] as? String ?: "",
            image = recipeMap["image"] as? String ?: "",
            ingredients = (recipeMap["ingredients"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            tags = (recipeMap["tags"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            owner = recipeMap["owner"] as? String ?: "",
            likes = (recipeMap["likes"] as? Number)?.toInt() ?: 0
        )
    }

    fun deleteRecipe(recipeId: String) {
        viewModelScope.launch {
            val success = repository.deleteRecipe(recipeId)
            if (success) fetchRecipes()
        }
    }

    fun fetchRandomRecipe(uid: String) : Boolean {
        return try {
            viewModelScope.launch {
                val fetchedApiRecipe = recipeRepository.getRandomRecipe()
                if (fetchedApiRecipe == null) {
                    return@launch
                }

                val fetchedRecipe = toRecipe(fetchedApiRecipe, uid)
                val success = repository.addRecipe(fetchedRecipe)
                if (success) fetchRecipes()
            }
            true
        } catch (e: Exception) {
            Log.e("RecipeViewModel", "Error fetching random recipe", e)
            false
        }
    }

    fun toRecipe(apiRecipeD: ApiRecipeD, uid: String): Recipe {
        val ingredientsList = mutableListOf<String>()

        for (i in 1..20) {
            val fieldName = "strIngredient$i"

            val field = apiRecipeD.javaClass.getDeclaredField(fieldName)
            field.isAccessible = true
            val ingredient = field.get(apiRecipeD) as? String

            if (!ingredient.isNullOrBlank()) {
                ingredientsList.add(ingredient)
            }
        }
        return Recipe(
            id = apiRecipeD.idMeal ?: LocalDateTime.now().toString(),
            title = apiRecipeD.strMeal ?: "Unknown Title",
            image = apiRecipeD.strMealThumb ?: "",
            ingredients = ingredientsList,
            owner = uid,
            likes = 0
        )
    }

    fun fetchCommentsForRecipe(recipeId: String) {
        viewModelScope.launch {
            try {
                val commentsList = commentRepository.getCommentsForRecipe(recipeId)
                _comments.value = commentsList
                Log.d("viewmodel",commentsList.toString())
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error fetching comments for recipe $recipeId", e)
            }
        }
    }

}