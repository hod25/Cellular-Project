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
import androidx.fragment.app.activityViewModels
import com.example.myapplication.ViewModels.RecipeViewModel
import com.example.myapplication.ViewModels.TagsViewModel

class TagFragment : Fragment() {

    private lateinit var selectedTagsGridLayout: GridLayout
    private lateinit var addTagButton: Button
    private val selectedTags = mutableListOf<String>()
    private val tagsViewModel: TagsViewModel by activityViewModels()
    private lateinit var recipeViewModel: RecipeViewModel

    private val defaultTags = listOf(
        "milk", "gluten", "meat","eggs","sugar"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tagslist, container, false)

        selectedTagsGridLayout = view.findViewById(R.id.tagContainer)
        addTagButton = view.findViewById(R.id.addTagButton)
        recipeViewModel = RecipeViewModel()
        updateTagsInGrid()

        addTagButton.setOnClickListener {
            showTagSelectionDialog()
        }

        return view
    }

    private fun updateTagsInGrid() {
        selectedTagsGridLayout.removeAllViews()

        for (tag in selectedTags) {
            val button = Button(context)
            button.text = tag

            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadius = 48f
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
            button.setOnClickListener {
                val index = selectedTags.indexOf(tag)
                if (index != -1) {
                    selectedTags.removeAt(index)
                    tagsViewModel.setTags(selectedTags)
                    updateTagsInGrid()
                }
            }
            tagsViewModel.setTags(selectedTags)
            selectedTagsGridLayout.addView(button)
        }
    }

    private fun showTagSelectionDialog() {
        val tagOptions = defaultTags.toTypedArray()
        val selectedItems = BooleanArray(tagOptions.size)

        AlertDialog.Builder(requireContext())
            .setTitle("Choose Tag")
            .setMultiChoiceItems(tagOptions, selectedItems) { _, which, isChecked ->
                if (isChecked) {
                    selectedTags.add(tagOptions[which])
                } else {
                    selectedTags.remove(tagOptions[which])
                }
            }
            .setPositiveButton("Confirm") { _, _ ->
                updateTagsInGrid()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}