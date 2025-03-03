package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.model.User
import com.example.myapplication.model.RegisterViewModel
import kotlinx.coroutines.launch

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firstNameField = view.findViewById<EditText>(R.id.firstNameRegister)
        val lastNameField = view.findViewById<EditText>(R.id.lastNameRegister)
        val emailField = view.findViewById<EditText>(R.id.emailRegister)
        val passwordField = view.findViewById<EditText>(R.id.passwordRegister)
        val signUpButton = view.findViewById<Button>(R.id.signUpButtonRegister)

        signUpButton.setOnClickListener {
            val firstName = firstNameField.text.toString().trim()
            val lastName = lastNameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isNotEmpty() && password.length >= 6) {
                // הפעלת פעולת רישום בקורוטינה
                lifecycleScope.launch {
                    // קריאה ל-ViewModel להרשמה ושמירה
                    registerViewModel.registerUser(email, password, firstName, lastName)
                }
            } else {
                Toast.makeText(requireContext(), "אנא מלא את כל הפרטים", Toast.LENGTH_SHORT).show()
            }
        }

        // מאזין לתוצאה של ההרשמה
        registerViewModel.registerResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "הרשמה הצליחה!", Toast.LENGTH_SHORT).show()
                // אפשר להוסיף כאן מעבר למסך אחר
            } else {
                // אם ההרשמה נכשלה
                val errorMessage = registerViewModel.errorMessage.value ?: "הרשמה נכשלה"
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
