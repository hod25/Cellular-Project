package com.example.myapplication.Adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import com.example.myapplication.CommentsListFragment
import com.example.myapplication.FeedFragmentDirections
import com.example.myapplication.R
import com.example.myapplication.RecipePreviewFragment
import com.example.myapplication.model.Comment
import com.example.myapplication.model.RecipePreview
import com.google.ai.client.generativeai.type.content
import java.util.ArrayList

class FeedAdapter(
    private val context: Context,
    var recipes: List<RecipePreview>,
    private val fragmentManager: FragmentManager
) : BaseAdapter() {

    override fun getCount(): Int {
        return recipes.size
    }

    override fun getItem(position: Int): Any {
        return recipes[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.fragment_recipepreview, parent, false)

        val recipe = recipes[position]

        val titleTextView: TextView = view.findViewById(R.id.title)
        titleTextView.text = recipe.title

        val imageView: ImageView = view.findViewById(R.id.image)
        imageView.setImageResource(R.drawable.pesto)

        // הצגת התגובות ישירות בפרגמנט
        val recipePreviewFragment = RecipePreviewFragment()
        recipePreviewFragment.setRecipe(recipe)

        // הצגת הפרגמנט בתוך התצוגה
        //val commentAdapter = CommentsAdapter(context, recipe.comments) // ייתכן שתצטרך ליצור את CommentAdapter
        //recipePreviewFragment.commentsListView?.adapter = commentAdapter

        view.setOnClickListener {
            Log.d("click","recipe "+recipe.id)
            val navController = (context as? androidx.fragment.app.FragmentActivity)
                ?.findNavController(R.id.nav_host_fragment)
            val action = FeedFragmentDirections.actionFeedFragmentToViewRecipeFragment(recipe.id)
            // שליחת ה-ID של המתכון ל-ViewRecipeFragment
            navController?.navigate(action)
        }

        return view
    }

    fun updateRecipes(recipes: List<RecipePreview>) {
        this.recipes = recipes
        notifyDataSetChanged()
    }
}
