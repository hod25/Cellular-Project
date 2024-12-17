package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class CreateRecipeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createrecipe)

        val listView: ListView = findViewById(R.id.ingredients)

        // רשימת פריטים
        val items = listOf("Item 1", "Item 2", "Item 3")

        // מתאם
        val adapter = ArrayAdapter(this, R.layout.ingerdient, R.id.itemTextView, items)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, view, position, _ ->
            val checkBox = view.findViewById<CheckBox>(R.id.itemCheckBox)
            checkBox.isChecked = !checkBox.isChecked
        }
    }
}
