package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.Adapter.CommentsAdapter
import com.example.myapplication.model.Comment
import com.example.myapplication.model.CommentViewModel
import com.example.myapplication.model.RecipeViewModel
import android.widget.ListView
import androidx.fragment.app.replace

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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp() // חזרה לדף הקודם
        }

        recipeViewModel.selectedRecipe.observe(viewLifecycleOwner) { recipe ->
            recipe?.let {
                recipeTitle.text = it.title
                recipeImage.setImageResource(R.drawable.pesto)

                val ingredientsFragment = IngredientsListFragment().apply {
                    arguments = Bundle().apply {
                        putStringArrayList("ingredients", ArrayList(it.ingredients))
                        putBoolean("isViewRecipe", true) // מעביר את המידע אם מדובר ב-ViewRecipe
                    }
                }
                val tagsFragment = StaticTagsFragment().apply {
                    arguments = Bundle().apply {
                        putStringArrayList("tags", ArrayList(it.tags))
                        putBoolean("isViewRecipe", true) // מעביר את המידע אם מדובר ב-ViewRecipe
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

                // אפשר להוסיף כאן עדכון לייקים במסד נתונים או ב-ViewModel
                recipeViewModel.addLike(recipeId)
                likeClicked = true;
            }
        }
    }
}
