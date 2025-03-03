package com.example.myapplication.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val userRepository = UserRepository() // יצירת אובייקט של ה-UserRepository
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private var viewModelJob: Job? = null

    // פונקציה להתחברות
    fun loginUser(email: String, password: String) {
        // אם יש כבר עבודה בקורוטינה, עצור אותה
        viewModelJob?.cancel()

        // יצירת קורוטינה חדשה
        viewModelJob = CoroutineScope(Dispatchers.IO).launch {
            val result = userRepository.loginUser(email, password)

            // עדכון ב-UI אחרי שהקורוטינה מסתיימת
            if (result) {
                _loginResult.postValue(true)
            } else {
                _errorMessage.postValue("התחברות נכשלה")
                _loginResult.postValue(false)
            }
        }
    }

    // הפסקת כל העבודה בקורוטינה במקרה של שינוי מצב
    override fun onCleared() {
        super.onCleared()
        viewModelJob?.cancel()
    }
}
