package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication.Adapter.CommentsAdapter
import com.example.myapplication.model.Comment

class CommentsListFragment (val comments : List<Comment>) : Fragment() {
    constructor() : this(emptyList())

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

       val adapter = CommentsAdapter(requireContext(), comments)
        commentsListView.adapter = adapter

        heartImageView.setOnClickListener {
            val currentLikes = likes.text.toString().toInt()
            likes.text = (currentLikes + 1).toString()
        }
    }
}
