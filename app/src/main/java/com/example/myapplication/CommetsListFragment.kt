package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication.utils.extensions.Comment

class CommentsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_commentslist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val commentsListView: ListView = view.findViewById(R.id.comments)
        val heartImageView: ImageView = view.findViewById(R.id.imageView)
        val likes: TextView = view.findViewById(R.id.likes)

        val comments = listOf(
            Comment("comment 1", R.drawable.avatar),
            Comment("comment 2", R.drawable.avatar),
            Comment("comment 3", R.drawable.avatar),
            Comment("comment 4", R.drawable.avatar)
        )

       val adapter = CommentsAdapter(requireContext(), comments)
        commentsListView.adapter = adapter

        heartImageView.setOnClickListener {
            val currentLikes = likes.text.toString().toInt()
            likes.text = (currentLikes + 1).toString()
        }
    }
}
