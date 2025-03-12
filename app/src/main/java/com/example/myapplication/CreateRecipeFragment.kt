package com.example.myapplication

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
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_createrecipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.userId.observe(viewLifecycleOwner) { uid ->
            userId = uid
        }
        val ingredientsFragment = IngredientsListFragment().apply {
            arguments = Bundle().apply {
                putBoolean("isViewRecipe", false) // לא מדובר ב-ViewRecipe
            }
        }

        // חיפוש רכיבים עם בדיקה למניעת קריסה
        val deleteIcon = view.findViewById<ImageView>(R.id.deleteRecipe)
        val editTextTitle = view.findViewById<EditText>(R.id.recipeName)
        val buttonSave = view.findViewById<Button>(R.id.save)
        val buttonSurpriseMe = view.findViewById<Button>(R.id.surpriseMe)
        imagePreview = view.findViewById(R.id.imagePreview)

        deleteIcon?.setOnClickListener {
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

        // מאזין לשינויים ברשימת המרכיבים
        ingredientsViewModel.ingredients.observe(viewLifecycleOwner) { ingredients ->
            ingredientsList = ingredients
        }

        // מאזין לשינויים ברשימת התגיות
        tagsViewModel.tags.observe(viewLifecycleOwner) { tags ->
            tagsList = tags
            Log.d("TagsViewModel", "Updated tags: $tags")
        }

        // אתחול של בוחר תמונות
        selectImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                imageUri?.let { imagePreview.setImageURI(it) }
            }
        }

        imagePreview.setOnClickListener {
            openImagePicker()
        }

        buttonSurpriseMe.setOnClickListener{
            val succeed = recipeViewModel.fetchRandomRecipe(userId ?: "Unknown")
            if(succeed) {
                Toast.makeText(requireContext(), "Added random recipe from api", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.feedFragment)
            }
            else
                Toast.makeText(requireContext(), "Adding random recipe failed", Toast.LENGTH_SHORT).show()
        }



        buttonSave?.setOnClickListener {
            val title = editTextTitle?.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val recipe = Recipe(
                title = title,
                ingredients = ingredientsList,
                tags = tagsList,
                owner = userId ?: "Unknown",
                likes = 0
            )

            recipeViewModel.addRecipe(recipe)
            Toast.makeText(requireContext(), "Recipe saved successfully", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.feedFragment)
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        selectImageLauncher.launch(intent)
    }
}
