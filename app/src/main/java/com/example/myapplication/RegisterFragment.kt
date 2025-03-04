package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.model.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private val registerViewModel: RegisterViewModel by viewModels()

    // הגדרת ה-ProgressBar
    private lateinit var progressBar: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // מציאת ה-ProgressBar ב-XML
        progressBar = view.findViewById(R.id.progressBar) // חשוב לוודא ש-id הוא progressBar ב-XML

        val firstNameField = view.findViewById<EditText>(R.id.firstNameRegister)
        val lastNameField = view.findViewById<EditText>(R.id.lastNameRegister)
        val emailField = view.findViewById<EditText>(R.id.emailRegister)
        val passwordField = view.findViewById<EditText>(R.id.passwordRegister)
        val confirmPasswordField = view.findViewById<EditText>(R.id.confirmPasswordRegister)  // שדה לאימות סיסמא
        val signUpButton = view.findViewById<Button>(R.id.signUpButtonRegister)
        val alreadyHaveAccount = view.findViewById<TextView>(R.id.alreadyHaveAccount)

        signUpButton.setOnClickListener {
            val firstName = firstNameField.text.toString().trim()
            val lastName = lastNameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val confirmPassword = confirmPasswordField.text.toString().trim()

            // בדוק אם הסיסמאות תואמות
            if (password != confirmPassword) {
                Toast.makeText(requireContext(), "הסיסמאות אינן תואמות", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isNotEmpty() && password.length >= 6) {
                // הצגת ה-ProgressBar
                showProgressBar()

                // ביצוע רישום באמצעות FirebaseAuth
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        // הסתרת ה-ProgressBar אחרי שהרישום הושלם
                        hideProgressBar()

                        if (task.isSuccessful) {
                            // אם ההרשמה הצליחה, נרצה לחבר את המשתמש
                            val user = FirebaseAuth.getInstance().currentUser
                            if (user != null) {
                                Toast.makeText(requireContext(), "הרשמה הצליחה!", Toast.LENGTH_SHORT).show()

                                // ניווט לפיד או למסך הבית
                                findNavController().navigate(R.id.feedFragment) // שנה ל-feedFragment שלך
                            }
                        } else {
                            // אם קרתה שגיאה בהרשמה
                            val errorMessage = task.exception?.message ?: "הרשמה נכשלה"
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "אנא מלא את כל הפרטים", Toast.LENGTH_SHORT).show()
            }
        }

        // ה-`OnClickListener` של כפתור "יש לך כבר חשבון"
        alreadyHaveAccount.setOnClickListener {
            // ניווט למסך ההתחברות
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    // פונקציה להצגת ה-ProgressBar
    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    // פונקציה להסתיר את ה-ProgressBar
    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }
}
