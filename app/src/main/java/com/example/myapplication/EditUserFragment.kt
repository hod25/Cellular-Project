package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.myapplication.ViewModels.UserViewModel
import com.example.myapplication.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditUserFragment : Fragment() {

    private lateinit var imagePreview: ImageView
    private lateinit var selectImageLauncher: ActivityResultLauncher<Intent>

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var logoutButton: Button  // כפתור ההתנתקות

    private val userRepository = UserRepository()
    private var currentUserId: String? = null
    private val userViewModel = UserViewModel()

    private var imageUri: Uri? = null
    var imageUrl:String=""
    private var isImageChanged = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edituser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imagePreview = view.findViewById(R.id.imagePreview)

        firstNameEditText = view.findViewById(R.id.firstNameEditUser)
        lastNameEditText = view.findViewById(R.id.lastNameEditUser)
        saveButton = view.findViewById(R.id.saveUserChanges)
        logoutButton = view.findViewById(R.id.logoutButton)  // אתחול כפתור ההתנתקות

        // Fetch the current user ID
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        currentUserId?.let {
            fetchUserData(it)
        }

        saveButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                saveUserChanges()
            }
        }

        logoutButton.setOnClickListener {
            logoutUser()
        }

        imagePreview.setOnClickListener {
            openImagePicker()
        }

        selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri = result.data?.data
                imageUri?.let {
                    imagePreview.setImageURI(it)
                    isImageChanged = true
                }
            }
        }
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()

        (activity as? MainActivity)?.hideBottomNavigation()

        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.loginFragment)
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
                imageUrl = it["image"] as? String?:""
                if (!imageUrl.isNullOrEmpty()) {
                    Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.avatar)
                        .error(R.drawable.avatar)
                        .into(imagePreview)
                }
            }
        }
    }

    private suspend fun saveUserChanges() {
        val firstName = firstNameEditText.text.toString()
        val lastName = lastNameEditText.text.toString()

        currentUserId?.let {
            imageUrl = if (isImageChanged) {
                imageUri?.let { uri ->
                    userViewModel.uploadImageAndGetUrl(requireContext(),uri.toString())
                }?:""
            } else {
                imageUrl
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val success = userRepository.saveUserData(it, firstName, lastName, FirebaseAuth.getInstance().currentUser?.email ?: "",imageUrl?:"")
                withContext(Dispatchers.Main) {
                    if (success == true) {
                        Toast.makeText(requireContext(),"User data updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(),"Failed to update user data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


}
