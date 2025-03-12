package com.example.myapplication.model.networking

import com.example.myapplication.model.ApiRecipeD

data class RecipeApiResponse(
    val meals: List<ApiRecipeD>? = null
)
