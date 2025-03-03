package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.myapplication.model.Recipe

class RecipePreviewFragment : Fragment() {

    private lateinit var recipe: Recipe

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipepreview, container, false)
    }

    fun setRecipe(recipe: Recipe) {
        this.recipe = recipe
        view?.let { updateUI() }
    }

    private fun updateUI() {
        // הצגת כותרת המתכון
        val title: EditText = requireView().findViewById(R.id.title)
        title.setText(recipe.title)

        // הצגת התגיות ב-StaticTagsFragment
        val tagsFragment = StaticTagsFragment.newInstance(recipe.tags)
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.tagFragment, tagsFragment)
        transaction.commit()
    }
}
