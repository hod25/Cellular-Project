package com.example.myapplication.model

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
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

    private val recipeRepository = RecipeRepository()  // אתה יכול גם להשתמש ב-APIRepository אם יש לך
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

    // פונקציה להעלות את הקובץ ל-Cloudinary
    private suspend fun uploadImageAndGetUrl(image: File): String? {
        return try {
            // העלאת התמונה ל-Cloudinary ומקבלת את ה-URL
            cloudinaryRepository.uploadImage(image, collectionName)?.replace("http://", "https://") // שלח את הקונטקסט כאן
        } catch (e: Exception) {
            Log.e("CloudinaryError", "Failed to upload image", e)
            null
        }
    }

    fun searchRecipes(query: String) {
        val lowerCaseQuery = query.lowercase()

        // המרת המתכונים מ-Recipe ל-RecipePreview
        val previewRecipes = castRecipeToPreview(recipes.value ?: emptyList())

        // חיפוש לפי כותרת או לפי תגיות
        val filteredList = previewRecipes.filter { recipe ->
            recipe.title.lowercase().contains(lowerCaseQuery) ||
                    recipe.tags.any { it.lowercase().contains(lowerCaseQuery) }
        }

        _filteredRecipes.value = filteredList
    }

    fun filterRecipesByTags(tags: List<String>) {
        _recipes.value = emptyList()

        viewModelScope.launch {
            Log.d("recipes",_recipes.value.toString())
            val recipeList = recipeRepository.filterRecipesByTags(tags)
            _recipes.value = recipeList
            Log.d("recipes",_recipes.value.toString())
        }
    }

    // 1️⃣ שליפת מתכונים מה-Repository
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
                comments = listOf() // אפשר להוסיף את התגובות אם יש לך מידע עליהם
            )
        }
        return recipePreviews
    }

    // 2️⃣ הוספת מתכון
    /*fun addRecipe(context: Context, recipe: Recipe) {
        viewModelScope.launch {
            val success = repository.addRecipe(recipe)
            if (success) fetchRecipes() // ריענון הנתונים אחרי הוספה
        }
        uploadRecipeImage(recipe.image)
    }*/

    fun addRecipe(context: Context, recipe: Recipe) {
        CoroutineScope(Dispatchers.IO).launch {
            // העלאת התמונה
            val imageFile = getFileFromUri(context,recipe.image) // נניח שהתמונה שמורה כבר כ-File
            val imageUrl = imageFile?.let { uploadImageAndGetUrl(it) }

            // עדכון ה-Recipe עם ה-URL של התמונה
            val updatedRecipe = imageUrl?.let { recipe.copy(image = it) }

            // הוספת המתכון ל-Repository
            val success = updatedRecipe?.let { repository.addRecipe(it) }
            if (success == true) fetchRecipes() // ריענון הנתונים אחרי הוספה
        }
    }

    fun addLike(id:String) {
        viewModelScope.launch {
            val success = repository.addLike(id)
            if (success) fetchRecipes()
        }
    }

    // 3️⃣ עדכון מתכון
    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            val success = repository.updateRecipe(recipe)
            if (success) fetchRecipes()
        }
    }

    fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            try {
                val recipeMap = repository.getRecipe(recipeId) // מחזיר Map<String, Any>
                if (recipeMap != null) {
                    val recipe = mapToRecipe(recipeMap) // המרת ה-Map לאובייקט Recipe
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

    // 4️⃣ מחיקת מתכון
    fun deleteRecipe(recipeId: String) {
        viewModelScope.launch {
            val success = repository.deleteRecipe(recipeId)
            if (success) fetchRecipes()
        }
    }

    fun fetchRandomRecipe(uid: String) : Boolean {
        return try {
            viewModelScope.launch {
                    // שליפת המתכון האקראי מה-Repository
                    val fetchedApiRecipe = recipeRepository.getRandomRecipe()
                    Log.d("mashoo", fetchedApiRecipe.toString())
                    if (fetchedApiRecipe == null) {
                        return@launch
                    }
                    // המרת המתכון מ-ApiRecipeD ל-Recipe
                    val fetchedRecipe = toRecipe(fetchedApiRecipe, uid)
                    val success = repository.addRecipe(fetchedRecipe)
                    if (success) fetchRecipes() // ריענון הנתונים אחרי הוספה
            }
            true
        } catch (e: Exception) {
            Log.e("RecipeViewModel", "Error fetching random recipe", e)
            false
        }
    }

    fun toRecipe(apiRecipeD: ApiRecipeD, uid: String): Recipe {
        // יצירת רשימה ריקה של מרכיבים
        val ingredientsList = mutableListOf<String>()

        // איסוף המרכיבים מתוך שדות כמו strIngredient1, strIngredient2... strIngredient20
        for (i in 1..20) {
            // יצירת שם השדה על פי מספר המרכיב
            val fieldName = "strIngredient$i"

            // שים לב, זה מבוצע בעזרת reflection
            val field = apiRecipeD.javaClass.getDeclaredField(fieldName)
            field.isAccessible = true // מאפשר גישה לשדה, גם אם הוא לא ציבורי
            val ingredient = field.get(apiRecipeD) as? String

            // אם יש מרכיב, מוסיפים אותו לרשימה
            if (!ingredient.isNullOrBlank()) {
                ingredientsList.add(ingredient)
            }
        }

        // טיפול בבעיה אם strMeal או idMeal מגיעים כ-null
        Log.d("Ingredients", (apiRecipeD.idMeal == null).toString())

        // יצירת אובייקט Recipe עם ערך ברירת מחדל אם השדה null
        return Recipe(
            id = apiRecipeD.idMeal ?: LocalDateTime.now().toString(),  // אם idMeal null, נשתמש ב-LocalDateTime
            title = apiRecipeD.strMeal ?: "Unknown Title",  // אם strMeal null, נשתמש ב-"Unknown Title"
            image = apiRecipeD.strMealThumb ?: "",  // אם strMealThumb null, נשתמש במיתר ריק
            ingredients = ingredientsList,  // רשימת המרכיבים המורכבת
            owner = uid,
            likes = 0
        )
    }

    fun fetchCommentsForRecipe(recipeId: String) {
        viewModelScope.launch {
            try {
                val commentsList = commentRepository.getCommentsForRecipe(recipeId)
                _comments.value = commentsList // עדכון ה-LiveData ישירות ברשימת התגובות
                Log.d("viewmodel",commentsList.toString())
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error fetching comments for recipe $recipeId", e)
            }
        }
    }

}