package com.example.myapplication.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repository.UserRepository
import kotlinx.coroutines.launch

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
            // מבצע את ההתחברות ומקבל את ה-UID של המשתמש אם הצליח
            val uid = userRepository.loginUser(email, password)

            if (uid != null) {
                _userId.postValue(uid) // שמירת ה-UID של המשתמש
                _loginResult.postValue(true)
            } else {
                _errorMessage.postValue("התחברות נכשלה")
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
