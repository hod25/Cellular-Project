package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.setFragmentResultListener
import com.example.myapplication.Adapter.FeedAdapter
import com.example.myapplication.model.Comment
import com.example.myapplication.model.RecipeViewModel

class FeedFragment : Fragment() {
    private lateinit var viewModel: RecipeViewModel
    private lateinit var listView: ListView
    private lateinit var adapter: FeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        listView = view.findViewById(R.id.recipes)
        viewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)

        adapter = FeedAdapter(requireContext(), emptyList(), childFragmentManager)
        listView.adapter = adapter

        // האזנה לתוצאות כדי לוודא עדכון של הרשימה כשחוזרים אחורה
        parentFragmentManager.setFragmentResultListener("refreshFeed", this) { _, _ ->
            refreshFeed()
        }
        Log.d("createview","created")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshFeed()
    }

    override fun onResume() {
        super.onResume()
        refreshFeed()
    }

    private fun refreshFeed() {
        viewModel.fetchRecipes() // שליפה מחדש מה-ViewModel
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            // פעולות ברגע שמגיעים נתונים חדשים
            val recipePreviews = recipes?.let { viewModel.castRecipeToPreview(it) }
            if (recipePreviews != null) {
                // עדכון התגובות עבור כל מתכון
                //recipePreviews.forEach { recipePreview ->
                    //viewModel.fetchCommentsForRecipe(recipePreview.id)
                //}
                // עדכון הרשימה של המתכונים
                adapter.updateRecipes(recipePreviews)
                adapter.notifyDataSetChanged() // עדכון ה-ListView
            }
        }
    }
}
