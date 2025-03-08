package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication.Adapter.CommentsAdapter
import com.example.myapplication.model.Recipe
import com.example.myapplication.model.RecipePreview

class RecipePreviewFragment : Fragment() {

    private var commentsAdapter: CommentsAdapter? = null
    var commentsListView: ListView? = null
    private lateinit var likesTextView: TextView
    private var recipe: RecipePreview? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipepreview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // אתחול התצוגה
        commentsListView = view.findViewById(R.id.comments)
        likesTextView = view.findViewById(R.id.likes)

        val heartImageView: ImageView = view.findViewById(R.id.imageView)
        heartImageView.setOnClickListener {
            val currentLikes = likesTextView.text.toString().toInt()
            likesTextView.text = (currentLikes + 1).toString()
        }

        /*// אתחול האדפטר עם רשימה ריקה
        commentsAdapter = CommentsAdapter(requireContext(), emptyList())
        commentsListView?.adapter = commentsAdapter
        // אם יש מתכון, נעדכן את הרשימה
        recipe?.let {
            // רק אם ה-Fragment מוצמד לפעילות, אתחול את האדפטר
            if (isAdded) {
                commentsAdapter?.comments = it.comments
                commentsListView?.adapter = commentsAdapter
                commentsAdapter?.notifyDataSetChanged()
            }
        }*/
    }

    // פונקציה שתעדכן את המתכון ואת התגובות
    fun setRecipe(recipe: RecipePreview) {
        this.recipe = recipe

        // אם ה-Fragment מוצמד לפעילות, נעדכן את האדפטר
        //if (isAdded) {
            //commentsAdapter?.comments = recipe.comments
            //commentsAdapter?.notifyDataSetChanged()
        //}
    }
}


