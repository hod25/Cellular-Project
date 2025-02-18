package com.example.myapplication

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

class CreateRecipeFragment : Fragment(R.layout.fragment_createrecipe) {

    private val sharedViewModel: RecipeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_createrecipe, container, false)

        val deleteIcon: ImageView = view.findViewById(R.id.deleteRecipe)
        deleteIcon.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Are you sure you want to delete the recipe?")
                .setPositiveButton("Yes") { _, _ ->
                    findNavController().navigate(R.id.mainFragment)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        return view
    }
}
