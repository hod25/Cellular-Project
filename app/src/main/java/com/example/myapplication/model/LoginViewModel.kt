package com.example.myapplication.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repository.UserRepository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> get() = _userId

    // פונקציה להתחברות
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            val uid = withContext(Dispatchers.IO) { userRepository.loginUser(email, password) }

            if (uid != null) {
                _userId.postValue(uid) // שמירת ה-UID של המשתמש
                _loginResult.postValue(true)
            } else {
                _errorMessage.postValue("Authentication Failed")
                _loginResult.postValue(false)
            }
        }
    }


    // פונקציה להתנתקות
    fun logoutUser() {
        userRepository.auth.signOut()
        _userId.postValue(null)
        _loginResult.postValue(false)
    }
}
