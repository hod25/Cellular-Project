package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.widget.Button

class MainFragment: Fragment(R.layout.fragment_main) {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.loginButton).setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
        }

        view.findViewById<Button>(R.id.registerButton).setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_registerFragment)
        }

        view.findViewById<Button>(R.id.editUserButton).setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_editUserFragment)
        }

        view.findViewById<Button>(R.id.createRecipeButton).setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_createRecipeFragment)
        }

        view.findViewById<Button>(R.id.FeedButton).setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_feedFragment)
        }
    }
}
