package com.example.myapplication.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.repository.CommentRepository
import com.example.myapplication.repository.RecipeRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class RecipeViewModel : ViewModel() {

    private val repository = RecipeRepository()

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


    // 1️⃣ שליפת מתכונים מה-Repository
    fun fetchRecipes() {
        viewModelScope.launch {
            val recipeList = repository.getAllRecipes()

            _recipes.value = recipeList
        }
    }

    fun castRecipeToPreview(recipes:List<Recipe>) : List<RecipePreview> {
        val recipePreviews = recipes.map { recipe ->
            RecipePreview(
                id = recipe.id,
                title = recipe.title,
                imageRes = R.drawable.pesto, // או השתמש בתמונה לפי ה-URL, תוכל להוריד את התמונה ולהמיר אותה ל-Drawable
                tags = recipe.tags,
                comments = listOf() // אפשר להוסיף את התגובות אם יש לך מידע עליהם
            )
        }
        return recipePreviews
    }

    // 2️⃣ הוספת מתכון
    fun addRecipe(recipe: Recipe) {
        viewModelScope.launch {
            val success = repository.addRecipe(recipe)
            if (success) fetchRecipes() // ריענון הנתונים אחרי הוספה
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
//            image = recipeMap["image"] as? String ?: "",
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

    fun fetchRandomRecipe(uid: String) {
        viewModelScope.launch {
            try {
                // שליפת המתכון האקראי מה-Repository
                val fetchedApiRecipe = recipeRepository.getRandomRecipe()
                //Log.d("mashoo", fetchedApiRecipe.toString())
                if (fetchedApiRecipe == null) {
                    return@launch
                }
                // המרת המתכון מ-ApiRecipeD ל-Recipe
                val fetchedRecipe = toRecipe(fetchedApiRecipe,uid)
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error fetching random recipe", e)
            }
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