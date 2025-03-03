package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.activity.result.ActivityResult
import com.example.myapplication.Adapter.TagsAdapter

class EditUserFragment : Fragment() {
    private lateinit var selectedTagsContainer: GridLayout
    private val selectedTags = mutableListOf<String>()
    private lateinit var tagSelectionContainer: LinearLayout
    private lateinit var tagSelectionRecyclerView: RecyclerView
    private lateinit var addTagButton: Button
    private lateinit var imagePreview: ImageView
    private lateinit var selectImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edituser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedTagsContainer = view.findViewById(R.id.tagsRecyclerView)
        tagSelectionContainer = view.findViewById(R.id.tagSelectionContainer)
        addTagButton = view.findViewById(R.id.addTagButton)
        tagSelectionRecyclerView = view.findViewById(R.id.tagSelectionRecyclerView)
        tagSelectionRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = TagsAdapter(mutableListOf("Vegetarian", "Gluten", "Vegan", "Lactose"))
        tagSelectionRecyclerView.adapter = adapter

        adapter.setOnItemClickListener { tag ->
            toggleTagSelection()
            addTagToSelection(tag)
        }

        addTagButton.setOnClickListener {
            toggleTagSelection()
        }

        imagePreview = view.findViewById(R.id.imagePreview)
        selectImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageUri: Uri? = data?.data
                imageUri?.let { imagePreview.setImageURI(it) }
            }
        }

        imagePreview.setOnClickListener {
            openImagePicker()
        }
    }

    private fun toggleTagSelection() {
        tagSelectionContainer.visibility =
            if (tagSelectionContainer.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun addTagToSelection(tag: String) {
        selectedTags.add(tag)

        val tagView = TextView(requireContext()).apply {
            text = tag
            setPadding(16, 8, 16, 8)
            textSize = 16F
        }
        if (tagView.layoutParams == null) {
            tagView.layoutParams = GridLayout.LayoutParams()
        }

        val params = tagView.layoutParams as GridLayout.LayoutParams
        params.setMargins(16, 16, 16, 16)
        tagView.layoutParams = params
        tagView.setBackgroundResource(R.drawable.tags)
        tagView.setTextColor(Color.WHITE)

        selectedTagsContainer.addView(tagView)
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        selectImageLauncher.launch(intent)
    }
}
