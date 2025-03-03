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
    import androidx.fragment.app.viewModels

    class IngredientsListFragment : Fragment(R.layout.fragment_ingredientlist) {

        private val sharedViewModel: IngredientsViewModel by activityViewModels()
        private lateinit var adapter: IngredientAdapter
        private val items = mutableListOf<String>()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_ingredientlist, container, false)
            val listView: ListView = view.findViewById(R.id.ingredients)
            val addIngredientButton: Button = view.findViewById(R.id.addIngredientButton)

            adapter = IngredientAdapter(requireContext(), items, sharedViewModel)
            listView.adapter = adapter

            sharedViewModel.ingredients.observe(viewLifecycleOwner) { newIngredients ->
                items.clear()
                items.addAll(newIngredients)
                adapter.notifyDataSetChanged()
            }

            addIngredientButton.setOnClickListener {
                showAddIngredientDialog()
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
        private val sharedViewModel: IngredientsViewModel
    ) : ArrayAdapter<String>(context, R.layout.ingredient, ingredients) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.ingredient, parent, false)

            val ingredient = ingredients[position]
            val itemTextView: TextView = view.findViewById(R.id.itemTextView)
            val deleteIcon: ImageView = view.findViewById(R.id.deleteIcon)

            itemTextView.text = ingredient

            deleteIcon.setOnClickListener {
                ingredients.removeAt(position)
                notifyDataSetChanged()

                sharedViewModel.removeIngredient(ingredient)
            }

            return view
        }
    }
