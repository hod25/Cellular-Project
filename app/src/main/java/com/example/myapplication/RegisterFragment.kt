package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var progressBar: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progressBar)

        val emailField = view.findViewById<EditText>(R.id.emailRegister)
        val passwordField = view.findViewById<EditText>(R.id.passwordRegister)
        val confirmPasswordField = view.findViewById<EditText>(R.id.confirmPasswordRegister)
        val signUpButton = view.findViewById<Button>(R.id.signUpButtonRegister)
        val alreadyHaveAccount = view.findViewById<TextView>(R.id.alreadyHaveAccount)

        signUpButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val confirmPassword = confirmPasswordField.text.toString().trim()

            if (password != confirmPassword) {
                Toast.makeText(requireContext(), "הסיסמאות אינן תואמות", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isNotEmpty() && password.length >= 6) {
                showProgressBar()

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        hideProgressBar()

                        if (task.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser

                            if (user != null) {
                                Toast.makeText(requireContext(), "Registered Successfully", Toast.LENGTH_SHORT).show()

                                (activity as? MainActivity)?.showBottomNavigation()

                                findNavController().navigate(R.id.loginFragment)
                            }
                        } else {
                            val errorMessage = task.exception?.message ?: "Register Failed"
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Please Fill In All The Details", Toast.LENGTH_SHORT).show()
            }
        }

        alreadyHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }
}
