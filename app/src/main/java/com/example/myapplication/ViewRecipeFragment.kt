package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myapplication.Adapter.CommentsAdapter
import com.example.myapplication.model.Comment
import com.example.myapplication.ViewModels.CommentViewModel
import com.example.myapplication.ViewModels.RecipeViewModel
import android.widget.ListView
import com.squareup.picasso.Picasso

class ViewRecipeFragment : Fragment(R.layout.fragment_viewrecipe) {

    private lateinit var recipeId: String
    private lateinit var recipeTitle: TextView
    private lateinit var recipeImage: ImageView
    private lateinit var comment: EditText
    private lateinit var saveComment: Button
    private lateinit var commentsListView: ListView
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var likesTextView: TextView
    private lateinit var heartImageView: ImageView
    private val commentViewModel : CommentViewModel by activityViewModels()
    private val recipeViewModel: RecipeViewModel by activityViewModels()
    private var likeClicked: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeTitle = view.findViewById(R.id.recipeName)
        recipeImage = view.findViewById(R.id.imagePreview)
        heartImageView = view.findViewById(R.id.imageView)
        likesTextView = view.findViewById(R.id.likes)
        val ingredientsFragmentContainer: View = view.findViewById(R.id.ingredientList)
        val tagsFragmentContainer: View = view.findViewById(R.id.tagsList)
        recipeId = arguments?.getString("recipeId") ?: return

        recipeViewModel.loadRecipe(recipeId)
        recipeViewModel.selectedRecipe.observe(viewLifecycleOwner) { recipe ->
            recipe?.let {
                recipeTitle.text = it.title
                Picasso.get()
                    .load(if (!recipe.image.isNullOrEmpty()) recipe.image else null)
                    .placeholder(R.drawable.pesto)
                    .error(R.drawable.pesto)
                    .fit()
                    .centerCrop()
                    .into(recipeImage)

                val ingredientsFragment = IngredientsListFragment().apply {
                    arguments = Bundle().apply {
                        putStringArrayList("ingredients", ArrayList(it.ingredients))
                        putBoolean("isViewRecipe", true)
                    }
                }
                val tagsFragment = StaticTagsFragment().apply {
                    arguments = Bundle().apply {
                        putStringArrayList("tags", ArrayList(it.tags))
                        putBoolean("isViewRecipe", true)
                    }
                }
                likesTextView.text = it.likes.toString()
                childFragmentManager.beginTransaction()
                    .replace(ingredientsFragmentContainer.id, ingredientsFragment)
                    .replace(tagsFragmentContainer.id,tagsFragment)
                    .commit()
            }
        }

        comment = view.findViewById(R.id.comment)
        saveComment = view.findViewById(R.id.SaveComment)
        saveComment.setOnClickListener {
            val commentContent = comment.text.toString().trim()

            if (commentContent.isNotEmpty()) {
                val newComment = Comment(
                    id = "",
                    recipe = recipeId,
                    content = commentContent,
                    imageResId = -1
                )

                commentViewModel.addComment(newComment)
            } else {
                Toast.makeText(requireContext(), "Please enter a comment", Toast.LENGTH_SHORT).show()
            }
        }

        commentsAdapter = CommentsAdapter(requireContext(), emptyList())
        commentsListView = view.findViewById(R.id.comments)
        commentsListView.adapter = commentsAdapter
        recipeViewModel.fetchCommentsForRecipe(recipeId)

        recipeViewModel.comments.observe(viewLifecycleOwner) { comments ->
            commentsAdapter.comments = comments
            commentsAdapter.notifyDataSetChanged()
            Log.d("adapter",commentsAdapter.comments.toString())
        }


        heartImageView.setOnClickListener {
            if (!likeClicked) {
                val currentLikes = likesTextView.text.toString().toInt()
                val newLikes = currentLikes + 1
                likesTextView.text = newLikes.toString()

                recipeViewModel.addLike(recipeId)
                likeClicked = true;
            }
        }
    }
}
