package com.example.myapplication.model

data class RecipePreview(
    val id: String,
    val title: String,
    val imageUrl: String,
    val tags: List<String>,
    var comments: List<Comment>
)