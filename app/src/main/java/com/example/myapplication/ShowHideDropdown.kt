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

        // init
        tagSelectionContainer = findViewById(R.id.tagSelectionContainer)
        addTagButton = findViewById(R.id.addTagButton)
        tagSelectionRecyclerView = findViewById(R.id.tagSelectionRecyclerView)

        // add listener
        addTagButton.setOnClickListener {
            toggleTagSelection()
        }

        // create adapter
        setupTagSelectionRecyclerView()
    }

    fun toggleTagSelection() {
        // if the list is shon we hide it
        if (tagSelectionContainer.visibility == View.VISIBLE) {
            tagSelectionContainer.visibility = View.GONE
        } else {
            // else we show it
            tagSelectionContainer.visibility = View.VISIBLE
        }
    }

    private fun setupTagSelectionRecyclerView() {
        tagSelectionRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = TagSelectionAdapter(this)
        tagSelectionRecyclerView.adapter = adapter
    }

    fun hideTagSelection() {
        tagSelectionContainer.visibility = View.GONE
    }

}
