package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.Adapter.FeedAdapter
import com.example.myapplication.model.RecipeViewModel
import com.example.myapplication.model.UserViewModel

class FeedFragment : Fragment() {
    private lateinit var viewModel: RecipeViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var listView: ListView
    private lateinit var adapter: FeedAdapter
    private lateinit var searchLine: EditText
    private lateinit var myMeals: TextView


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        listView = view.findViewById(R.id.recipes)
        viewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)

        adapter = FeedAdapter(requireContext(), emptyList())
        listView.adapter = adapter
        searchLine = view.findViewById(R.id.searchLine)
        searchLine.addTextChangedListener { charSequence ->
            viewModel.searchRecipes(charSequence.toString())
        }

        myMeals = view.findViewById(R.id.myMeals)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // האזנה לתוצאות כדי לוודא עדכון של הרשימה כשחוזרים אחורה
        parentFragmentManager.setFragmentResultListener("refreshFeed", this) { _, _ ->
            refreshFeed()
        }
        viewModel.fetchRecipes() // שליפה מחדש מה-ViewModel
        refreshFeed()
        myMeals.setOnClickListener {
            userViewModel.userId.value?.let { it1 -> viewModel.fetchMyRecipes(it1) }
            userViewModel.userId.value?.let { it1 -> Log.d("uid", it1) }
        }
    }

    private fun refreshFeed() {
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            // פעולות ברגע שמגיעים נתונים חדשים
            val recipePreviews = recipes?.let { viewModel.castRecipeToPreview(it) }
            if (recipePreviews != null) {
                adapter.updateRecipes(recipePreviews)
                adapter.notifyDataSetChanged() // עדכון ה-ListView
            }
        }
        viewModel.filteredRecipes.observe(viewLifecycleOwner) { filteredRecipes ->
            if (filteredRecipes != null) {
                adapter.updateRecipes(filteredRecipes)
                adapter.notifyDataSetChanged()
            }
        }
    }
}
