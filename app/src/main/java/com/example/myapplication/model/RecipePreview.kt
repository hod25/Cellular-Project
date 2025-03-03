package com.example.myapplication.model

data class RecipePreview(
    val title: String,
    val imageRes: Int, // אם אתה משתמש בתמונה מתוך ה-res
    val tags: List<String>, // רשימת תגיות
    val comments: List<String> // רשימת תגובות
)