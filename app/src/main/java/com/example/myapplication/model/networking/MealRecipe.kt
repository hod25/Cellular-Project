package com.example.myapplication.model.networking

data class MealRecipe(
        val idMeal: String,  // ID של המתכון
        val strMeal: String, // שם המתכון
        val ingredients: List<String> // רשימה של מרכיבים
)