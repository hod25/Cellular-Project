package com.example.myapplication.model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repository.UserRepository
import kotlinx.coroutines.launch

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

    // אתחול - בדיקת משתמש מחובר
    init {
        checkCurrentUser()
    }

    // בדיקה אם יש משתמש מחובר
    private fun checkCurrentUser() {
        val currentUser = userRepository.auth.currentUser
        if (currentUser != null) {
            _userId.value = currentUser.uid
            loadUserData(currentUser.uid)
        }
    }

    // פונקציה לטעינת פרטי המשתמש
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

    fun uploadImage(imageUri: Uri, userId: String) {
        viewModelScope.launch {
            val imageUrl = userRepository.uploadImageToCloudinary(imageUri)
            if (imageUrl != null) {
                userRepository.saveUserImage(userId, imageUrl)
                _uploadImageUrl.postValue(imageUrl)
            }
        }
    }

    private fun saveImageUrlToFirestore(imageUrl: String?, currentUserId: String?) {
        if (imageUrl == null || currentUserId == null) return

        viewModelScope.launch {
            val success = userRepository.saveUserImage(currentUserId, imageUrl)
            if (success) {
                println("Image URL saved successfully!")
            } else {
                println("Failed to save image URL")
            }
        }
    }

    // פונקציה להתנתקות
    fun logoutUser() {
        userRepository.auth.signOut()
        _userId.value = null
        _userName.value = null
    }
}
