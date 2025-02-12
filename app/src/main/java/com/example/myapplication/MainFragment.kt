package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class MainFragment : Fragment(R.layout.fragment_main) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        view.findViewById<Button>(R.id.loginButton).setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_loginFragment)
        }

        view.findViewById<Button>(R.id.registerButton).setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_registerFragment)
        }

        view.findViewById<Button>(R.id.editUserButton).setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_editUserFragment)
        }

        view.findViewById<Button>(R.id.createRecipeButton).setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_createRecipeFragment)
        }
    }
}