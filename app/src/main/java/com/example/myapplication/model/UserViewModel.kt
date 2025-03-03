package com.example.myapplication.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    // פונקציה להתנתקות
    fun logoutUser() {
        userRepository.auth.signOut()
        _userId.value = null
        _userName.value = null
    }
}
