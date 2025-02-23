package com.example.myapplication

import android.app.AlertDialog
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import androidx.fragment.app.Fragment

class StaticTagsFragment : Fragment(){
    private lateinit var selectedTagsGridLayout: GridLayout
    private val selectedTags = listOf("milk", "gluten", "meat")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statictags, container, false)

        selectedTagsGridLayout = view.findViewById(R.id.tagContainer)
        updateTagsInGrid()

        return view
    }

    private fun updateTagsInGrid() {
        selectedTagsGridLayout.removeAllViews()

        for (tag in selectedTags) {
            val button = Button(context)
            button.text = tag

            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadius = 48f // קביעת הרדיוס של הפינות
            shape.setStroke(2, resources.getColor(R.color.black))
            shape.setColor(resources.getColor(R.color.dark_green))

            button.background = shape

            button.setTextColor(resources.getColor(android.R.color.white))

            button.setShadowLayer(2f, 4f, 4f, resources.getColor(R.color.black))

            val params = GridLayout.LayoutParams()
            params.width = 220
            params.height = 108
            params.setMargins(8, 8, 8, 8)
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED,1)
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1)
            button.layoutParams = params

            selectedTagsGridLayout.addView(button)
        }
    }
}