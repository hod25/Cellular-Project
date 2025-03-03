package com.example.myapplication.model

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
//import com.cloudinary.android.policy.GlobalUploadPolicy
//import com.example.myapplication.base.myapplication
import com.example.myapplication.BuildConfig
import com.example.myapplication.repository.RecipeRepository
import com.example.myapplication.utils.extensions.toFile
import kotlinx.coroutines.launch
import java.io.File

//class RecipeViewModel : ViewModel() {
//
//    private val repository = RecipeRepository()
//
//    private val _recipes = MutableLiveData<List<Recipe>>()
//    val recipes: LiveData<List<Recipe>> = _recipes
//
//    private val _isLoading = MutableLiveData<Boolean>()
//    val isLoading: LiveData<Boolean> = _isLoading
//
//    init {
//        val config = mapOf(
//            "cloud_name" to BuildConfig.CLOUD_NAME,
//            "api_key" to BuildConfig.API_KEY,
//            "api_secret" to BuildConfig.API_SECRET
//        )
//
//        MyApplication.Globals.context?.let {
//            MediaManager.init(it, config)
//            MediaManager.get().globalUploadPolicy = GlobalUploadPolicy.defaultPolicy()
//        }
//    }
//
//    // 1️⃣ שליפת מתכונים
//    fun fetchRecipes() {
//        viewModelScope.launch {
//            _isLoading.value = true
//            _recipes.value = repository.getAllRecipes()
//            _isLoading.value = false
//        }
//    }
//
//    // 2️⃣ הוספת מתכון עם העלאת תמונה לקלאודינארי
//    fun addRecipe(recipe: Recipe, imageBitmap: Bitmap) {
//        _isLoading.value = true
//        uploadImage(imageBitmap, recipe.title, { imageUrl ->
//            viewModelScope.launch {
//                val newRecipe = recipe.copy(imageUrl = imageUrl)
//                val success = repository.addRecipe(newRecipe)
//                if (success) fetchRecipes()
//                _isLoading.value = false
//            }
//        }, { error ->
//            _isLoading.value = false
//        })
//    }
//
//    // 3️⃣ עדכון מתכון עם העלאת תמונה חדשה אם יש
//    fun updateRecipe(recipe: Recipe, newImageBitmap: Bitmap?) {
//        _isLoading.value = true
//        if (newImageBitmap != null) {
//            uploadImage(newImageBitmap, recipe.title, { newImageUrl ->
//                val updatedRecipe = recipe.copy(imageUrl = newImageUrl)
//                viewModelScope.launch {
//                    val success = repository.updateRecipe(updatedRecipe)
//                    if (success) fetchRecipes()
//                    _isLoading.value = false
//                }
//            }, { error ->
//                _isLoading.value = false
//            })
//        } else {
//            viewModelScope.launch {
//                val success = repository.updateRecipe(recipe)
//                if (success) fetchRecipes()
//                _isLoading.value = false
//            }
//        }
//    }
//
//    // 4️⃣ מחיקת מתכון
//    fun deleteRecipe(recipeId: String) {
//        viewModelScope.launch {
//            val success = repository.deleteRecipe(recipeId)
//            if (success) fetchRecipes()
//        }
//    }
//
//    // פונקציה להעלאת תמונה לקלאודינארי
//    private fun uploadImage(
//        bitmap: Bitmap,
//        name: String,
//        onSuccess: (String?) -> Unit,
//        onError: (String?) -> Unit
//    ) {
//        val context = MyApplication.Globals.context ?: return
//        val file: File = bitmap.toFile(context, name)
//
//        MediaManager.get().upload(file.path)
//            .option("folder", "recipes")
//            .callback(object : UploadCallback {
//                override fun onStart(requestId: String?) {}
//
//                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
//
//                override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
//                    val url = resultData["secure_url"] as? String ?: ""
//                    onSuccess(url)
//                }
//
//                override fun onError(requestId: String?, error: ErrorInfo?) {
//                    onError(error?.description ?: "Unknown error")
//                }
//
//                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
//            })
//            .dispatch()
//    }
//}
