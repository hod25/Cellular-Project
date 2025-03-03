package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.model.LoginViewModel
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailField = view.findViewById<EditText>(R.id.emailLogin)
        val passwordField = view.findViewById<EditText>(R.id.passwordLogin)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val forgotPasswordText = view.findViewById<TextView>(R.id.forgotPassword)

        // כשהמשתמש לוחץ על כפתור ההתחברות
        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // קריאה ל-ViewModel עם הקורוטינה
                loginViewModel.loginUser(email, password)
            } else {
                Toast.makeText(requireContext(), "אנא מלא את כל הפרטים", Toast.LENGTH_SHORT).show()
            }
        }

        // מאזין לתוצאה של ההתחברות
        loginViewModel.loginResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "התחברת בהצלחה!", Toast.LENGTH_SHORT).show()
                // ניווט למסך אחר, למשל מסך הבית
            } else {
                // הצגת שגיאה במידה וההתחברות נכשלה
                val errorMessage = loginViewModel.errorMessage.value ?: "התחברות נכשלה"
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        // פעולה אם המשתמש שכח סיסמה
        forgotPasswordText.setOnClickListener {
            // אפשר להוסיף אפשרות לשחזור סיסמא
            Toast.makeText(requireContext(), "שחזור סיסמה", Toast.LENGTH_SHORT).show()
        }
    }
}
