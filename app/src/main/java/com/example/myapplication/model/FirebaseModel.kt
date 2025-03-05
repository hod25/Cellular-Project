package com.example.myapplication.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.repository.RecipeRepository
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {

    private val repository = RecipeRepository()

    private val _recipes = MutableLiveData<List<Recipe>?>() // רשימת מתכונים
    val recipes: MutableLiveData<List<Recipe>?> = _recipes

    private val _selectedTags = MutableLiveData<List<String>>()
    val selectedTags: LiveData<List<String>> get() = _selectedTags

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

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
                    _recipes.postValue(listOf(recipe)) // מכניסים לרשימה ושולחים ל-LiveData
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
}
