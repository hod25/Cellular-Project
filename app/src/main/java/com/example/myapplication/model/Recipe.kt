package com.example.myapplication.model

data class Recipe(
    val id: String = "",
    val title: String = "",
    val image: String = "",
    val ingredients: List<String> = listOf(),
    val tags: List<String> = listOf(),
    val owner: String = "",
    val likes: Int = 0
)

