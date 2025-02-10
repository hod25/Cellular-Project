package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ListView
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class CreateRecipeFragment : Fragment(R.layout.fragment_createrecipe) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_createrecipe, container, false)

        val listView: ListView = view.findViewById(R.id.ingredient)

        val items = listOf("Item 1", "Item 2", "Item 3")

        val adapter = ArrayAdapter(requireContext(), R.layout.ingerdient, R.id.itemTextView, items)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, view, position, _ ->
            val checkBox = view.findViewById<CheckBox>(R.id.itemCheckBox)
            checkBox.isChecked = !checkBox.isChecked
        }

        return view
    }
}
