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

    private val _recipes = MutableLiveData<List<Recipe>>() // רשימת מתכונים
    val recipes: LiveData<List<Recipe>> = _recipes

    private val _selectedTags = MutableLiveData<List<String>>()
    val selectedTags: LiveData<List<String>> get() = _selectedTags

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

    // 4️⃣ מחיקת מתכון
    fun deleteRecipe(recipeId: String) {
        viewModelScope.launch {
            val success = repository.deleteRecipe(recipeId)
            if (success) fetchRecipes()
        }
    }
}
