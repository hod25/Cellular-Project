package com.example.myapplication

import android.app.Activity
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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.model.Recipe
import com.example.myapplication.model.RecipeViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.model.UserViewModel
import com.example.myapplication.utils.extensions.TagsViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class EditRecipeFragment : Fragment(R.layout.fragment_editrecipe) {

    private val ingredientsViewModel: IngredientsViewModel by activityViewModels()
    private val tagsViewModel: TagsViewModel by activityViewModels()
    private lateinit var recipeId: String
    private lateinit var selectImageLauncher: ActivityResultLauncher<Intent>
    private var imageUrl: String? = null
    private var imageUri: Uri? = null
    private val recipeViewModel: RecipeViewModel by activityViewModels()
    private var isImageChanged = false
    private var ingredientsList: List<String> = listOf()
    private var tagsList: List<String> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_editrecipe, container, false)

        recipeId = arguments?.getString("recipeId") ?: ""

        val titleEditText = view.findViewById<EditText>(R.id.recipeName)
        val saveButton = view.findViewById<Button>(R.id.save)
        val recipeImage = view.findViewById<ImageView>(R.id.imagePreview)
        val ingredientsFragmentContainer: View = view.findViewById(R.id.ingredientList)
        val tagsFragmentContainer: View = view.findViewById(R.id.tagsList)

        IngredientsListFragment().apply {
            arguments = Bundle().apply {
                putBoolean("isViewRecipe", false) // לא מדובר ב-ViewRecipe
            }
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

        recipeViewModel.loadRecipe(recipeId)

        recipeViewModel.selectedRecipe.observe(viewLifecycleOwner) { recipe ->
            recipe?.let {
                titleEditText.setText(it.title)
                imageUrl = it.image
                isImageChanged = false

                Picasso.get()
                    .load(it.image.takeUnless { it.isNullOrEmpty() })
                    .placeholder(R.drawable.pesto)
                    .error(R.drawable.pesto)
                    .fit()
                    .centerCrop()
                    .into(recipeImage)

                val ingredientsFragment = IngredientsListFragment().apply {
                    arguments = Bundle().apply {
                        putStringArrayList("ingredients", ArrayList(it.ingredients))
                        putBoolean("isViewRecipe", false)
                    }
                }
                val tagsFragment = StaticTagsFragment().apply {
                    arguments = Bundle().apply {
                        putStringArrayList("tags", ArrayList(it.tags))
                        putBoolean("isViewRecipe", false)
                    }
                }

                childFragmentManager.beginTransaction()
                    .replace(ingredientsFragmentContainer.id, ingredientsFragment)
                    .replace(tagsFragmentContainer.id, tagsFragment)
                    .commit()
            }
        }

        saveButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (isImageChanged && imageUri != null) {
                imageUrl = imageUri.toString()
            }
            lifecycleScope.launch {
                val updatedRecipe = Recipe(
                    id = recipeId,
                    title = title,
                    ingredients = ingredientsList,
                    tags = tagsList,
                    owner = UserViewModel().userId.value ?: "Unknown",
                    likes = recipeViewModel.selectedRecipe.value?.likes ?: 0,
                    image = imageUrl ?: ""
                )
                recipeViewModel.updateRecipe(requireContext(), updatedRecipe)
                findNavController().navigate(R.id.feedFragment)
            }
        }

        selectImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri = result.data?.data
                imageUri?.let {
                    recipeImage.setImageURI(it)
                    isImageChanged = true
                }
            }
        }

        recipeImage.setOnClickListener {
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
