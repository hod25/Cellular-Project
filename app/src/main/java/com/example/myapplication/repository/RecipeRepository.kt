package com.example.myapplication.repository

import android.util.Log
import com.example.myapplication.model.ApiRecipeD
import com.example.myapplication.model.Recipe
import com.example.myapplication.model.networking.RecipeApiResponse
import com.example.myapplication.model.networking.RetrofitInstance.api
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class RecipeRepository {

    private val database = Firebase.firestore
    private val collectionName = "Recipes"

    suspend fun getAllRecipes(): List<Recipe> {
        return try {
            val snapshot = database.collection(collectionName).get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Recipe::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e("Firebase", "Error fetching recipes", e)
            emptyList()
        }
    }

    suspend fun getUserRecipes(userId: String): List<Recipe> {
        return try {
            val result = database.collection(collectionName)
                .whereEqualTo("owner", userId) // מסנן לפי בעל המתכון
                .get()
                .await()

            result.documents.mapNotNull { it.toObject(Recipe::class.java) }
        } catch (e: Exception) {
            Log.e("Firebase", "Error fetching user recipes", e)
            emptyList()
        }
    }

    suspend fun addRecipe(recipe: Recipe): Boolean {
        return try {
            val docRef = database.collection("Recipes").add(recipe).await()
            docRef.update("id", docRef.id) // 🔹 עדכון ה-ID במסמך
            true
        } catch (e: Exception) {
            Log.e("Firebase", "Error adding recipe", e)
            false
        }
    }

    suspend fun updateRecipe(recipe: Recipe): Boolean {
        return try {
            database.collection(collectionName).document(recipe.id).set(recipe).await()
            true
        } catch (e: Exception) {
            Log.e("Firebase", "Error updating recipe", e)
            false
        }
    }

    suspend fun getRecipe(recipeId: String): Map<String, Any>?  {
        return try {
            val document = database.collection("Recipes").document(recipeId).get().await()
            if (document.exists()) document.data else null
        } catch (e: Exception) {
            Log.e("Firebase", "Error fetching recipe", e)
            null
        }
    }

    suspend fun deleteRecipe(recipeId: String): Boolean {
        return try {
            database.collection(collectionName).document(recipeId).delete().await()
            true
        } catch (e: Exception) {
            Log.e("Firebase", "Error deleting recipe", e)
            false
        }
    }
    suspend fun getRandomRecipe(): ApiRecipeD? {
        return try {
            val response : RecipeApiResponse = api.getRandomRecipe()
            Log.d("API Response", response.toString())
            response.meals?.firstOrNull()
        } catch (e: Exception) {
            Log.e("API", "Error fetching random recipe from TheMealDB", e)
            null
        }
    }
    suspend fun filterRecipesByTags(tags: List<String>): List<Recipe> {
        return try {
            if (tags.isEmpty()) return emptyList() // ✅ מחזיר רשימה ריקה אם אין תגיות

            var query: Query = database.collection(collectionName)
            for (tag in tags) {
                query = query.whereArrayContains("tags", tag)
            }

            val result = query.get().await()

            result.documents.mapNotNull { it.toObject(Recipe::class.java) }
        } catch (e: Exception) {
            Log.e("Firebase", "Error fetching recipes", e)
            emptyList()
        }
    }

    suspend fun addLike(recipeId: String) : Boolean {
        return try {
            val recipeRef = database.collection(collectionName).document(recipeId)

            database.runTransaction { transaction ->
                val snapshot = transaction.get(recipeRef)
                val currentLikes = snapshot.getLong("likes") ?: 0
                transaction.update(recipeRef, "likes", currentLikes + 1)
            }
            return true
        } catch (e: Exception) {
            Log.e("Firebase", "Error adding like", e)
            false
        }
    }

}
