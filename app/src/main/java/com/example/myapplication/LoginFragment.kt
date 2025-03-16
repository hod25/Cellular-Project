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
import androidx.navigation.fragment.findNavController
import com.example.myapplication.ViewModels.LoginViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment(R.layout.fragment_login) {
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailField = view.findViewById<EditText>(R.id.emailLogin)
        val passwordField = view.findViewById<EditText>(R.id.passwordLogin)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val forgotPasswordText = view.findViewById<TextView>(R.id.forgotPassword)

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                showProgressBar()
                loginViewModel.loginUser(email, password)
            }
            else {
                Toast.makeText(requireContext(), "אנא מלא את כל הפרטים", Toast.LENGTH_SHORT).show()
            }
        }

        loginViewModel.loginResult.observe(viewLifecycleOwner) { success ->
            hideProgressBar()

            if (success) {
                Toast.makeText(requireContext(), "התחברת בהצלחה!", Toast.LENGTH_SHORT).show()
                (requireActivity() as MainActivity).showBottomNavigation()
                findNavController().navigate(R.id.feedFragment)
            }
            else {
                val errorMessage = loginViewModel.errorMessage.value ?: "התחברות נכשלה"
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        val dontHaveAccountText = view.findViewById<TextView>(R.id.dontHaveAccount)

        dontHaveAccountText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        forgotPasswordText.setOnClickListener {
            val email = emailField.text.toString().trim()

            if (email.isNotEmpty()) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "מייל לשחזור סיסמה נשלח בהצלחה!", Toast.LENGTH_SHORT).show()
                        } else {
                            val errorMessage = task.exception?.message ?: "שגיאה בשליחת המייל לשחזור סיסמה"
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "אנא הזן את המייל שלך", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProgressBar() {
        view?.findViewById<ProgressBar>(R.id.progressBar)?.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        view?.findViewById<ProgressBar>(R.id.progressBar)?.visibility = View.GONE
    }
}
