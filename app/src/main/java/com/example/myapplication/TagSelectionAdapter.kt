    package com.example.myapplication

    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.TextView
    import androidx.recyclerview.widget.RecyclerView

    class TagSelectionAdapter(private val context: ShowHideDropdown) : RecyclerView.Adapter<TagSelectionAdapter.TagViewHolder>() {

        private val tags = listOf("Tag 1", "Tag 2", "Tag 3", "Tag 4")  // רשימת תגיות

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            return TagViewHolder(view)
        }

        override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
            holder.bind(tags[position])
        }

        override fun getItemCount(): Int {
            return tags.size
        }

        inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bind(tag: String) {
                (itemView as TextView).text = tag
                itemView.setOnClickListener {
                    // פעולה כאשר לוחצים על תגית
                    context.hideTagSelection() // קריאה לפונקציה שמסתירה את הרשימה
                }
            }
        }
    }
