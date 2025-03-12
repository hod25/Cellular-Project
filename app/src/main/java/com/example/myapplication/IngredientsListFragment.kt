package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class IngredientsListFragment : Fragment(R.layout.fragment_ingredientlist) {

    private val sharedViewModel: IngredientsViewModel by activityViewModels()
    private lateinit var adapter: IngredientAdapter
    private val items = mutableListOf<String>()
    private var isViewRecipe: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ingredientlist, container, false)
        val listView: ListView = view.findViewById(R.id.ingredients)
        val addIngredientButton: Button? = view.findViewById(R.id.addIngredientButton)

        // קביעה אם מדובר ב- ViewRecipe או CreateRecipe
        isViewRecipe = arguments?.getBoolean("isViewRecipe", false) ?: false

        // הסתרת הכפתור אם מדובר ב-ViewRecipe
        addIngredientButton?.visibility = if (isViewRecipe) View.GONE else View.VISIBLE

        // קביעה אם מדובר ב- ViewRecipe או CreateRecipe
        isViewRecipe = arguments?.getBoolean("isViewRecipe", false) ?: false

        adapter = IngredientAdapter(requireContext(), items, sharedViewModel, isViewRecipe)
        listView.adapter = adapter

        // טוען רכיבים מתוך ה- arguments אם קיימים (לשימוש ב- ViewRecipeFragment)
        arguments?.getStringArrayList("ingredients")?.let {
            items.clear()
            items.addAll(it)
            adapter.notifyDataSetChanged()
        }

        // טוען רכיבים מ- ViewModel אם אין רשימה ב- arguments (לשימוש ב- CreateRecipeFragment)
        sharedViewModel.ingredients.observe(viewLifecycleOwner) { newIngredients ->
            if (arguments == null || arguments?.getStringArrayList("ingredients") == null) {
                items.clear()
                items.addAll(newIngredients)
                adapter.notifyDataSetChanged()
            }
        }

        // הצגת כפתור הוספה רק במסך יצירת מתכון
        if (addIngredientButton != null && !isViewRecipe) {
            addIngredientButton.setOnClickListener {
                showAddIngredientDialog()
            }
        }

        return view
    }

    private fun showAddIngredientDialog() {
        val input = EditText(requireContext())
        input.hint = "Enter ingredient name"

        AlertDialog.Builder(requireContext())
            .setTitle("Add Ingredient")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val newIngredient = input.text.toString().trim()
                if (newIngredient.isNotEmpty()) {
                    sharedViewModel.addIngredient(newIngredient)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}


class IngredientAdapter(
    context: Context,
    private val ingredients: MutableList<String>,
    private val sharedViewModel: IngredientsViewModel,
    private val isViewRecipe: Boolean // פרמטר נוסף שמציין אם מדובר ב-ViewRecipe או CreateRecipe
) : ArrayAdapter<String>(context, R.layout.ingredient, ingredients) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.ingredient, parent, false)

        val ingredient = ingredients[position]
        val itemTextView: TextView = view.findViewById(R.id.itemTextView)
        val deleteIcon: ImageView = view.findViewById(R.id.deleteIcon)

        itemTextView.text = ingredient

        // אם אנחנו במסך "צפייה במתכון" (ViewRecipe), נסתיר את כפתור המחיקה
        if (isViewRecipe) {
            deleteIcon.visibility = View.GONE
        } else {
            deleteIcon.setOnClickListener {
                ingredients.removeAt(position)
                notifyDataSetChanged()
                sharedViewModel.removeIngredient(ingredient)
            }
        }

        return view
    }
}
