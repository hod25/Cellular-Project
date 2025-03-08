package com.example.myapplication.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.myapplication.R
import com.example.myapplication.model.Comment

class CommentsAdapter(private val context: Context, var comments: List<Comment>) : BaseAdapter() {
    override fun getCount(): Int {
        return comments.size
    }

    override fun getItem(position: Int): Any {
        return comments[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.comment, parent, false)
        val textView: TextView = view.findViewById(R.id.content)
        val imageView: ImageView = view.findViewById(R.id.commentOwnerImg)

        val comment = comments[position]
        textView.text = comment.content
        // טיפול במקרה של ערך imageResId == -1
        if (comment.imageResId != -1) {
            imageView.setImageResource(comment.imageResId)
        } else {
            imageView.setImageResource(R.drawable.avatar) // תוכל להגדיר תמונה ברירת מחדל
        }

        return view
    }
}
