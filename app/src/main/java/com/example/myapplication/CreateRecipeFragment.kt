package com.example.myapplication

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.model.Recipe
import com.example.myapplication.model.RecipeViewModel
import com.example.myapplication.model.UserViewModel
import com.example.myapplication.utils.extensions.TagsViewModel

class CreateRecipeFragment : Fragment(R.layout.fragment_createrecipe) {

    private val ingredientsViewModel: IngredientsViewModel by activityViewModels()
    private val tagsViewModel: TagsViewModel by activityViewModels()
    private val recipeViewModel: RecipeViewModel by viewModels()
    private lateinit var imagePreview: ImageView
    private lateinit var selectImageLauncher: ActivityResultLauncher<Intent>
    private var ingredientsList: List<String> = listOf()
    private var tagsList: List<String> = listOf()
    private val userViewModel: UserViewModel by activityViewModels()
    private var userId: String? = null  // שמירת UID של המשתמש המחובר

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_createrecipe, container, false)

        // קבלת UID של המשתמש המחובר
        userViewModel.userId.observe(viewLifecycleOwner) { uid ->
            userId = uid
        }

        val deleteIcon: ImageView = view.findViewById(R.id.deleteRecipe)
        deleteIcon.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Are you sure you want to delete the recipe?")
                .setPositiveButton("Yes") { _, _ ->
                    ingredientsViewModel.removeAllIngredients()
                    findNavController().navigate(R.id.mainFragment)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        ingredientsViewModel.ingredients.observe(viewLifecycleOwner) { ingredients ->
            ingredientsList = ingredients
        }

        tagsViewModel.tags.observe(viewLifecycleOwner) { tags ->
            tagsList = tags
            Log.d("TagsViewModel", "Updated tags: $tags")
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

        val editTextTitle = view.findViewById<EditText>(R.id.recipeName)
        val buttonSave = view.findViewById<Button>(R.id.save)

        buttonSave.setOnClickListener {
            val title = editTextTitle.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val recipe = Recipe(
                title = title,
                ingredients = ingredientsList,
                tags = tagsList,
                owner = userId ?: "Unknown", // אם אין UID נכניס "Unknown"
                likes = 0
            )

            recipeViewModel.addRecipe(recipe)
            Toast.makeText(requireContext(), "Recipe saved successfully", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        selectImageLauncher.launch(intent)
    }
}
