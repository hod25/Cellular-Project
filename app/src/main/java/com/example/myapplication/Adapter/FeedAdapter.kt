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
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.example.myapplication.FeedFragmentDirections
import com.example.myapplication.R
import com.example.myapplication.model.RecipePreview
import com.example.myapplication.model.RecipeViewModel
import com.example.myapplication.model.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.squareup.picasso.Picasso

class FeedAdapter(
    private val context: Context,
    var recipes: List<RecipePreview>,
    val recipeViewModel: RecipeViewModel = RecipeViewModel(),
    val userViewModel: UserViewModel = UserViewModel()


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
        Log.d("Image",recipe.imageUrl)

        val titleTextView: TextView = view.findViewById(R.id.title)
        titleTextView.text = recipe.title

        val imageView: ImageView = view.findViewById(R.id.image)
        Picasso.get()
            .load(if (!recipe.imageUrl.isNullOrEmpty()) recipe.imageUrl else null)
            .placeholder(R.drawable.pesto)
            .error(R.drawable.pesto)
            .fit()
            .centerCrop()
            .into(imageView)

        //val recipePreviewFragment = RecipePreviewFragment()
        //recipePreviewFragment.setRecipe(recipe)

        //val commentAdapter = CommentsAdapter(context, recipe.comments) // ייתכן שתצטרך ליצור את CommentAdapter
        //recipePreviewFragment.commentsListView?.adapter = commentAdapter

        view.setOnClickListener {
            Log.d("click","recipe "+recipe.id)
            val navController = (context as? androidx.fragment.app.FragmentActivity)
                ?.findNavController(R.id.nav_host_fragment)
            var action : NavDirections? = null
            val uid = userViewModel.userId.value
            if (uid != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (recipeViewModel.isRecipeMine(uid, recipe.id))
                        action =  FeedFragmentDirections.actionFeedFragmentToEditRecipeFragment(recipe.id)
                    else
                        action =  FeedFragmentDirections.actionFeedFragmentToViewRecipeFragment(recipe.id)
                    navController?.navigate(action?:FeedFragmentDirections.actionFeedFragmentToViewRecipeFragment(recipe.id))
                }
            }
        }

        return view
    }

    fun updateRecipes(recipes: List<RecipePreview>) {
        this.recipes = recipes
        notifyDataSetChanged()
    }
}
