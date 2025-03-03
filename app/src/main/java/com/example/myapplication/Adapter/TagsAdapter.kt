package com.example.myapplication.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TagsAdapter(private val tagsList: List<String>) : RecyclerView.Adapter<TagsAdapter.TagViewHolder>() {

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return TagViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tagsList[position]
        holder.tagText.text = tag
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(tag)
        }
    }

    override fun getItemCount(): Int {
        return tagsList.size
    }

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagText: TextView = itemView.findViewById(android.R.id.text1)
    }
}

