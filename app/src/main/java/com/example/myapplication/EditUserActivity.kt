package com.example.myapplication


import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginEnd
import androidx.core.view.marginLeft
import androidx.core.view.marginRight

class EditUserActivity : AppCompatActivity() {
    private lateinit var selectedTagsContainer: GridLayout
    private lateinit var tagSelectionContainer: LinearLayout
    private lateinit var tagSelectionRecyclerView: RecyclerView
    private lateinit var addTagButton: Button
    private val selectedTags = mutableListOf<String>() // רשימת תגיות שנבחרו

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edituser)

        // אתחול View-ים
        selectedTagsContainer = findViewById(R.id.tagsRecyclerView)
        //selectedTagsContainer.layoutManager = LinearLayoutManager(this)
        tagSelectionContainer = findViewById(R.id.tagSelectionContainer)
        addTagButton = findViewById(R.id.addTagButton)
        tagSelectionRecyclerView = findViewById(R.id.tagSelectionRecyclerView)

        // אתחול RecyclerView
        tagSelectionRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = TagsAdapter(mutableListOf("Vegeterian", "Gluten", "Vegan", "Lactose"))
        tagSelectionRecyclerView.adapter = adapter

        // הגדרת listener לתגיות
        adapter.setOnItemClickListener { tag ->
            toggleTagSelection() // הסתרת הרשימה
            addTagToSelection(tag) // הוספת התגית שנבחרה
        }

        // הצגת/הסתרת הרשימה
        addTagButton.setOnClickListener {
            toggleTagSelection()
        }
    }

    private fun toggleTagSelection() {
        tagSelectionContainer.visibility =
            if (tagSelectionContainer.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun addTagToSelection(tag: String) {
        // הוספת התגית לרשימה
        selectedTags.add(tag)

        // יצירת TextView לתגית
        val tagView = TextView(this).apply {
            text = tag
            setPadding(16, 8, 16, 8)
            textSize = 16F
        }
        // בדוק אם ה-LayoutParams לא מוגדר
        if (tagView.layoutParams == null) {
            tagView.layoutParams = GridLayout.LayoutParams()
        }

        val params = tagView.layoutParams as GridLayout.LayoutParams
        params.setMargins(16, 16, 16, 16)
        tagView.layoutParams = params
        tagView.setBackgroundResource(R.drawable.tags)
        tagView.setTextColor(Color.WHITE)

        // הוספת ה-TextView לקונטיינר
        selectedTagsContainer.addView(tagView)
    }
}

