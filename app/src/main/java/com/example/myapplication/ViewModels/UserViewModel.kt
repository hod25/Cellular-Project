package com.example.myapplication.ViewModels

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repository.CloudinaryRepository
import com.example.myapplication.repository.UserRepository
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class UserViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> get() = _userId

    private val _userName = MutableLiveData<String?>()
    val userName: LiveData<String?> get() = _userName

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _uploadImageUrl = MutableLiveData<String?>()
    val uploadImageUrl: LiveData<String?> get() = _uploadImageUrl

    val cloudinaryRepository = CloudinaryRepository()
    val collectionName = "UserImages"

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val currentUser = userRepository.auth.currentUser
        if (currentUser != null) {
            _userId.value = currentUser.uid
            loadUserData(currentUser.uid)
        }
    }

    private fun loadUserData(uid: String) {
        viewModelScope.launch {
            val user = userRepository.getUserData(uid)
            if (user != null) {
                _userName.postValue("${user["firstName"]} ${user["lastName"]}")
            } else {
                _errorMessage.postValue("שגיאה בטעינת הנתונים")
            }
        }
    }

    fun getFileFromUri(context: Context, uri: String): File? {
        val fileUri = Uri.parse(uri)
        val cursor = context.contentResolver.query(fileUri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val fileName = it.getString(columnIndex)
                val file = File(context.cacheDir, fileName)

                context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                return file
            }
        }
        return null
    }

    suspend fun uploadImageAndGetUrl(context:Context, image: String): String? {
        return try {
            val imageFile = getFileFromUri(context,image)
            if (imageFile != null) {
                cloudinaryRepository.uploadImage(imageFile, collectionName)?.replace("http://", "https://")
            }
            else
                null
        } catch (e: Exception) {
            Log.e("CloudinaryError", "Failed to upload image", e)
            null
        }
    }

    fun logoutUser() {
        userRepository.auth.signOut()
        _userId.value = null
        _userName.value = null
    }
}
