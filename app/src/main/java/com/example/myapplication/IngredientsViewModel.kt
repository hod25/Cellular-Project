package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class IngredientsViewModel : ViewModel() {
    private var _ingredients = MutableLiveData<List<String>>(emptyList())
    var ingredients: LiveData<List<String>> = _ingredients

    fun addIngredient(ingredient: String) {
        _ingredients.value = _ingredients.value?.toMutableList()?.apply { add(ingredient) }
    }

    fun removeIngredient(ingredient: String) {
        _ingredients.value = _ingredients.value?.toMutableList()?.apply { remove(ingredient) }
    }

    fun removeAllIngredients() {
        _ingredients.value = emptyList()
    }
    public fun setIngredients(newIngredients: List<String>) {
        _ingredients.value = newIngredients.toList() // מעדכן רשימה חדשה כדי שה-Observer יזהה שינוי
    }

}
