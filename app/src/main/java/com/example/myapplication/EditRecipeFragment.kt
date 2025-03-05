package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
import kotlinx.coroutines.launch

class EditRecipeFragment : Fragment(R.layout.fragment_editrecipe) {

    // שימוש ב-Safe Args לקבלת הערכים
//    private val args: EditRecipeFragmentArgs by navArgs()
    private lateinit var recipeId: String
    private lateinit var ingredientsList: List<String>
    private lateinit var tagsList: List<String>
    private var userId: String? = null
    private val recipeViewModel: RecipeViewModel by activityViewModels()

    private val db = Firebase.firestore // Firestore instance

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_editrecipe, container, false)

        // קבלת הנתונים מה-Safe Args (במקום ערך קשיח)
        recipeId = "AZRZSseSlTBzmltGeB5y" // args.recipeId

        // הגדרת השדות
        val titleEditText = view.findViewById<EditText>(R.id.recipeName)
        val saveButton = view.findViewById<Button>(R.id.save)

        // צפייה במתכון מתוך ה- ViewModel
        recipeViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipes?.let {
                val recipe = it.find { recipe -> recipe.id == recipeId }
                recipe?.let { recipe ->
                    titleEditText.setText(recipe.title)
                    ingredientsList = recipe.ingredients
                    tagsList = recipe.tags
                    userId = recipe.owner
                }
            }
        }

        // טעינת המתכון מה- Repository דרך ה- ViewModel
        recipeViewModel.loadRecipe(recipeId)

        // לחיצה על כפתור שמירה
        saveButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedRecipe = Recipe(
                id = recipeId, // הוספת ה-ID כדי לעדכן את הרשומה הנכונה
                title = title,
                ingredients = ingredientsList,
                tags = tagsList,
                owner = userId ?: "Unknown",
                likes = 0
            )

            // עדכון דרך ה- ViewModel
            recipeViewModel.updateRecipe(updatedRecipe)

            // מעבר למסך הראשי
            findNavController().navigate(R.id.feedFragment)
        }

        return view
    }

    // שליפת המתכון מ-Firebase
    suspend private fun loadRecipeFromFirebase() {
        val recipeRef = db.collection("recipes").document(recipeId)

        recipeRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("EditRecipeFragment", "DocumentSnapshot data: ${document}")
                    val recipe = document.toObject(Recipe::class.java)
                    // מלא את השדות עם נתוני המתכון הקיים
                    recipe?.let {
                        ingredientsList = it.ingredients
                        tagsList = it.tags
                        userId = it.owner

                        val titleEditText = view?.findViewById<EditText>(R.id.recipeName)
                        titleEditText?.setText(it.title)

                        // כאן תוכל להוסיף לוגיקה נוספת כדי למלא את שדות רכיבים ותגיות אם צריך
                    }
                } else {
                    Toast.makeText(requireContext(), "Recipe not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("EditRecipeFragment", "Error getting recipe: ", exception)
            }
    }

    // עדכון המתכון ב-Firebase
    private fun updateRecipeInFirebase(updatedRecipe: Recipe) {
        val recipeRef = db.collection("recipes").document(recipeId)

        recipeRef.set(updatedRecipe)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Recipe updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Log.d("EditRecipeFragment", "Error updating recipe: ", exception)
                Toast.makeText(requireContext(), "Error updating recipe", Toast.LENGTH_SHORT).show()
            }
    }
}
