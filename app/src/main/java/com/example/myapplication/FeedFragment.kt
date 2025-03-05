package com.example.myapplication

    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ArrayAdapter
    import android.widget.ListView
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.ViewModelProvider
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.example.myapplication.Adapter.FeedAdapter
    import com.example.myapplication.model.RecipePreview
    import com.example.myapplication.model.RecipeViewModel


class FeedFragment : Fragment() {
    private lateinit var viewModel: RecipeViewModel
    private lateinit var listView: ListView
    private lateinit var adapter: FeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        // חיבור ל-ListView ולא ל-RecyclerView
        listView = view.findViewById(R.id.recipes)

        viewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)

        // יצירת האדפטר ל-ListView
        adapter = FeedAdapter(requireContext(), emptyList(), childFragmentManager)
        listView.adapter = adapter

        // שליפה יזומה מה-ViewModel
        viewModel.fetchRecipes() // שליחה ל-ViewModel לשלוף נתונים מ-Firebase

        // עדכון הרשימה עם הנתונים שהתקבלו
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            var recipePreviews = recipes?.let { viewModel.castRecipeToPreview(it) }
            if (recipePreviews != null) {
                adapter.setRecipes(recipePreviews)
            }
        }

        return view
    }
}
