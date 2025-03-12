package com.example.myapplication.model.networking

import com.cloudinary.api.ApiResponse
import com.example.myapplication.model.ApiRecipeD
import com.example.myapplication.model.Recipe
import retrofit2.http.GET
import retrofit2.http.Query

// Interface של ה-API שלך
interface RecipeApi {
    @GET("random.php")
    suspend fun getRandomRecipe() : RecipeApiResponse
}
