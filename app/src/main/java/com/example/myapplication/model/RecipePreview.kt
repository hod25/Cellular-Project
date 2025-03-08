package com.example.myapplication.model

data class RecipePreview(
    val id: String,
    val title: String,
    val imageRes: Int,
    val tags: List<String>,
    var comments: List<Comment>
)