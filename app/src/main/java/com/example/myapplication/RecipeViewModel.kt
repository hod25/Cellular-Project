package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecipeViewModel : ViewModel() {
    private val _ingredients = MutableLiveData<List<String>>(emptyList())
    val ingredients: LiveData<List<String>> = _ingredients

    fun addIngredient(ingredient: String) {
        _ingredients.value = _ingredients.value?.toMutableList()?.apply { add(ingredient) }
    }

    fun removeIngredient(ingredient: String) {
        _ingredients.value = _ingredients.value?.toMutableList()?.apply { remove(ingredient) }
    }

    fun removeAll() {
        _ingredients.value = emptyList()
    }
}
