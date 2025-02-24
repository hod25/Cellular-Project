package com.example.myapplication.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Recipe
import com.example.myapplication.repository.RecipeRepository
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {

    private val repository = RecipeRepository()

    private val _recipes = MutableLiveData<List<Recipe>>() // רשימת מתכונים
    val recipes: LiveData<List<Recipe>> = _recipes

    private val _isLoading = MutableLiveData<Boolean>() // טעינה
    val isLoading: LiveData<Boolean> = _isLoading

    // 1️⃣ שליפת מתכונים מה-Repository
    fun fetchRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            _recipes.value = repository.getAllRecipes()
            _isLoading.value = false
        }
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
