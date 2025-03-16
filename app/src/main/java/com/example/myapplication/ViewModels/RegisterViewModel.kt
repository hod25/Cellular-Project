package com.example.myapplication.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _registerResult = MutableLiveData<Boolean>()
    val registerResult: LiveData<Boolean> get() = _registerResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun registerUser(email: String, password: String, firstName: String, lastName: String) {
        // הרצה בקורוטינה (לוגיקת קורוטינה)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val isRegistered = userRepository.registerUser(email, password)

                if (isRegistered) {
                    val firebaseUser = userRepository.auth.currentUser
                    firebaseUser?.let {
                        val isDataSaved = userRepository.saveUserData(it.uid, firstName, lastName, email, "")
                        if (isDataSaved) {
                            _registerResult.value = true
                        } else {
                            _errorMessage.value = "Failed to save user data"
                            _registerResult.value = false
                        }
                    }
                } else {
                    _errorMessage.value = "Registration failed"
                    _registerResult.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Unknown error"
                _registerResult.value = false
            }
        }
    }

}
