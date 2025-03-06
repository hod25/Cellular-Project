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
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.myapplication.Adapter.TagsAdapter
import com.example.myapplication.model.UserViewModel
import com.example.myapplication.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditUserFragment : Fragment() {
    private lateinit var selectedTagsContainer: GridLayout

    private val selectedTags = mutableListOf<String>()
    private lateinit var tagSelectionContainer: LinearLayout
    private lateinit var tagSelectionRecyclerView: RecyclerView
    private lateinit var addTagButton: Button
    private lateinit var imagePreview: ImageView
    private lateinit var selectImageLauncher: ActivityResultLauncher<Intent>

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var logoutButton: Button  // כפתור ההתנתקות

    private val userRepository = UserRepository()
    private var currentUserId: String? = null
    private val userViewModel = UserViewModel()

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
        imagePreview = view.findViewById(R.id.imagePreview)

        firstNameEditText = view.findViewById(R.id.firstNameEditUser)
        lastNameEditText = view.findViewById(R.id.lastNameEditUser)
        passwordEditText = view.findViewById(R.id.passwordEditUser)
        saveButton = view.findViewById(R.id.saveUserChanges)
        logoutButton = view.findViewById(R.id.logoutButton)  // אתחול כפתור ההתנתקות

        // Fetch the current user ID
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        currentUserId?.let {
            fetchUserData(it)
        }

        saveButton.setOnClickListener {
            saveUserChanges()
        }

        // הוספת מאזין לכפתור ההתנתקות
        logoutButton.setOnClickListener {
            logoutUser()
        }

        val adapter = TagsAdapter(mutableListOf("Vegetarian", "Gluten", "Vegan", "Lactose"))
        tagSelectionRecyclerView.adapter = adapter

        adapter.setOnItemClickListener { tag ->
            toggleTagSelection()
            addTagToSelection(tag)
        }

        addTagButton.setOnClickListener {
            toggleTagSelection()
        }

        imagePreview.setOnClickListener {
            openImagePicker()
        }

        selectImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageUri: Uri? = data?.data
                imageUri?.let { imagePreview.setImageURI(it) }
            }
        }
    }

    // כשיש לך את ה-logoutButton, כך תעשה את החלפת ה-Fragment
    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut() // התנתקות מהמשתמש

    // הסתרת ה-Bottom Navigation
    (activity as? MainActivity)?.hideBottomNavigation()

        // מעבר ל-LoginFragment דרך ה-NavController
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.loginFragment)
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

    private fun fetchUserData(uid: String) {
        // Fetch user data from Firestore
        viewLifecycleOwner.lifecycleScope.launch {
            val userData = userRepository.getUserData(uid)
            userData?.let {
                firstNameEditText.setText(it["firstName"] as? String ?: "")
                lastNameEditText.setText(it["lastName"] as? String ?: "")
                // Password not fetched due to security concerns
            }
        }
    }

    private fun saveUserChanges() {
        val firstName = firstNameEditText.text.toString()
        val lastName = lastNameEditText.text.toString()
        val password = passwordEditText.text.toString()

        val selectedImageUri: Uri? = null // פה אתה צריך לשמור את ה-URI של התמונה שנבחרה

        currentUserId?.let {
            // Update the user data


            viewLifecycleOwner.lifecycleScope.launch {
                val success = userRepository.saveUserData(it, firstName, lastName, FirebaseAuth.getInstance().currentUser?.email ?: "")
                if (success) {
                    println("User data updated successfully")
                } else {
                    println("Failed to update user data")
                }

                if (password.isNotEmpty()) {
                    // If password is entered, update it
                    updatePassword(password)
                }
                // 🔹 העלאת תמונה
                selectedImageUri?.let { uri ->
                    // 🔹 העלאת התמונה ל-Cloudinary
                    userViewModel.uploadImage(uri, it)
                }
            }
        }
    }

    private fun updatePassword(password: String) {
        // Update user password using FirebaseAuth
        FirebaseAuth.getInstance().currentUser?.updatePassword(password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Password updated successfully
                } else {
                    // Show error
                }
            }
    }
}
