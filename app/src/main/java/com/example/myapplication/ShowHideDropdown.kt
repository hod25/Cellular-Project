package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ShowHideDropdown: AppCompatActivity() {

    private lateinit var tagSelectionContainer: LinearLayout
    private lateinit var addTagButton: Button
    private lateinit var tagSelectionRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edituser)

        // אתחול המערכים
        tagSelectionContainer = findViewById(R.id.tagSelectionContainer)
        addTagButton = findViewById(R.id.addTagButton)
        tagSelectionRecyclerView = findViewById(R.id.tagSelectionRecyclerView)

        // הגדרת כפתור הוספת תגיות להציג את הרשימה
        addTagButton.setOnClickListener {
            toggleTagSelection()
        }

        // אתחול RecyclerView עם מחוון מתאים
        setupTagSelectionRecyclerView()
    }

    fun toggleTagSelection() {
        // אם הרשימה מוצגת, נסיר אותה
        if (tagSelectionContainer.visibility == View.VISIBLE) {
            tagSelectionContainer.visibility = View.GONE
        } else {
            // אחרת, נציג אותה
            tagSelectionContainer.visibility = View.VISIBLE
        }
    }

    private fun setupTagSelectionRecyclerView() {
        // יצירת אדפטר והגדרת ראה-בוקר (במקרה של משתנה מדגם פשוט)
        tagSelectionRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = TagSelectionAdapter(this)  // אדפטר עם נתונים לדוגמה
        tagSelectionRecyclerView.adapter = adapter
    }

    fun hideTagSelection() {
        tagSelectionContainer.visibility = View.GONE
    }

}
