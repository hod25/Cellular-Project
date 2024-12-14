package com.example.myapplication


import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class EditUserActivity : AppCompatActivity() {
    // the tags that the user chose 
    private lateinit var selectedTagsContainer: GridLayout
    private val selectedTags = mutableListOf<String>()
    
    // the tags list which the user choose from
    private lateinit var tagSelectionContainer: LinearLayout
    private lateinit var tagSelectionRecyclerView: RecyclerView

    // button for adding new tag
    private lateinit var addTagButton: Button

    // profile picture
    private lateinit var imagePreview: ImageView
    private lateinit var selectImageLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edituser)

        // init
        selectedTagsContainer = findViewById(R.id.tagsRecyclerView)
        tagSelectionContainer = findViewById(R.id.tagSelectionContainer)
        addTagButton = findViewById(R.id.addTagButton)
        tagSelectionRecyclerView = findViewById(R.id.tagSelectionRecyclerView)
        tagSelectionRecyclerView.layoutManager = LinearLayoutManager(this)

        // init adapter
        val adapter = TagsAdapter(mutableListOf("Vegeterian", "Gluten", "Vegan", "Lactose"))
        tagSelectionRecyclerView.adapter = adapter

        // listen to tag selection list
        adapter.setOnItemClickListener { tag ->
            toggleTagSelection() // הסתרת הרשימה
            addTagToSelection(tag) // הוספת התגית שנבחרה
        }

        // show list after clicking the button
        addTagButton.setOnClickListener {
            toggleTagSelection()
        }

        //init
        imagePreview = findViewById(R.id.imagePreview)
        selectImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val imageUri: Uri? = data?.data
                if (imageUri != null) {
                    // show selected image
                    imagePreview.setImageURI(imageUri)
                }
            }
        }

        // listen to the image
        imagePreview.setOnClickListener {
            openImagePicker()
        }
    }

    //show/hide selection list
    private fun toggleTagSelection() {
        tagSelectionContainer.visibility =
            if (tagSelectionContainer.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    // add a tag to the screen
    private fun addTagToSelection(tag: String) {
        // add to the list
        selectedTags.add(tag)

        // create new textview
        val tagView = TextView(this).apply {
            text = tag
            setPadding(16, 8, 16, 8)
            textSize = 16F
        }
        // create the layout if its not already exist
        if (tagView.layoutParams == null) {
            tagView.layoutParams = GridLayout.LayoutParams()
        }

        // define the params for the textview
        val params = tagView.layoutParams as GridLayout.LayoutParams
        params.setMargins(16, 16, 16, 16)
        tagView.layoutParams = params
        tagView.setBackgroundResource(R.drawable.tags)
        tagView.setTextColor(Color.WHITE)

        // add the textview to the screen
        selectedTagsContainer.addView(tagView)
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*" // choose file type
        selectImageLauncher.launch(intent)
    }
}

