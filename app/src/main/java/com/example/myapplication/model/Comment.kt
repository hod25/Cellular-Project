package com.example.myapplication.model

data class Comment(
    val id: String = "",   // הוספת ערך ברירת מחדל
    val recipe: String = "",
    val content: String = "",
    val imageResId: Int = -1
)
