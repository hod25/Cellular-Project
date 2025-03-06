package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myapplication.model.RecipeViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ViewRecipeFragment : Fragment(R.layout.fragment_viewrecipe) {

    private lateinit var recipeId: String
    private lateinit var recipeTitle: TextView
    private lateinit var recipeImage: ImageView
    private lateinit var recipeIngredients: TextView
    private lateinit var recipeTags: TextView

    private val recipeViewModel: RecipeViewModel by activityViewModels()
    private val db = Firebase.firestore // Firestore instance

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeTitle = view.findViewById(R.id.recipeName)
        recipeImage = view.findViewById(R.id.imagePreview)
//        recipeIngredients = view.findViewById(R.id.ingredientList)
//        recipeTags = view.findViewById(R.id.tagsList)
        // קבלת ה-ID של המתכון שהועבר
        recipeId = "AZRZSseSlTBzmltGeB5y" //arguments?.getString("recipeId") ?: return

        // טעינת הנתונים
        recipeViewModel.loadRecipe(recipeId)

        recipeViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            val recipe = recipes?.find { it.id == recipeId }
            recipe?.let {
                recipeTitle.text = it.title
//                recipeIngredients.text = it.ingredients.joinToString("\n")
//                recipeTags.text = it.tags.joinToString(", ")
            }
        }
    }
}
