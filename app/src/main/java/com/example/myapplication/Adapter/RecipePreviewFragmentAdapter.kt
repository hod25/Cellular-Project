package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.model.Recipe

class RecipePreviewFragmentAdapter(private val recipe: Recipe) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipepreview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = view.findViewById<EditText>(R.id.title)
        // הצגת מידע על המתכון
        title.setText(recipe.title)
        // לדוגמה, הצגת תמונה
        // image.setImageResource(recipe.image)

        // אם יש צורך להציג תגיות או תגובות, יש להוסיף את הקוד כאן
        // לדוגמה:
        // val tagAdapter = TagsAdapter(recipe.tags)
        // tagList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        // tagList.adapter = tagAdapter
    }
}
