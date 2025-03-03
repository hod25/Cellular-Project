package com.example.myapplication.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.example.myapplication.CommetsListFragment
import com.example.myapplication.R
import com.example.myapplication.StaticTagsFragment
import com.example.myapplication.model.Comment
import com.example.myapplication.model.RecipePreview

class FeedAdapter(
    private val context: Context,
    private var recipes: List<RecipePreview>,
    private val fragmentManager: FragmentManager // העברת ה-FragmentManager לאדפטר
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

        // הצגת התגיות
        val tagFragmentContainer: FragmentContainerView = view.findViewById(R.id.tagFragment)
        val tagFragmentTransaction = fragmentManager.beginTransaction()
        val staticTagsFragment = StaticTagsFragment.newInstance(recipe.tags)
        tagFragmentTransaction.replace(tagFragmentContainer.id, staticTagsFragment)
        tagFragmentTransaction.commit()

        // הצגת תגובות
        val commentsFragmentContainer: FragmentContainerView = view.findViewById(R.id.comments)
        val commentsFragmentTransaction = fragmentManager.beginTransaction()
        val commentList = recipe.comments.map { Comment(recipe = "", content = it, imageResId = R.drawable.pesto) }

        val commentsFragmentInstance = CommetsListFragment(commentList)
        commentsFragmentTransaction.replace(commentsFragmentContainer.id, commentsFragmentInstance)
        commentsFragmentTransaction.commit()

        return view
    }

    fun setRecipes(recipes: List<RecipePreview>) {
        this.recipes = recipes
        notifyDataSetChanged()
    }
}
