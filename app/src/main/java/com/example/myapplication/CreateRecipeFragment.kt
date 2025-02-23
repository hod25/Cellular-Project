package com.example.myapplication

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

class CreateRecipeFragment : Fragment(R.layout.fragment_createrecipe) {

    private val sharedViewModel: RecipeViewModel by activityViewModels()
    private lateinit var imagePreview: ImageView
    private lateinit var selectImageLauncher: ActivityResultLauncher<Intent>

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
                    sharedViewModel.removeAll()
                    findNavController().navigate(R.id.mainFragment)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        imagePreview = view.findViewById(R.id.imagePreview)
        selectImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageUri: Uri? = data?.data
                imageUri?.let { imagePreview.setImageURI(it) }
            }
        }

        imagePreview.setOnClickListener {
            openImagePicker()
        }

        return view
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        selectImageLauncher.launch(intent)
    }
}
