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
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.myapplication.Adapter.TagsAdapter
import com.example.myapplication.model.LoginViewModel
import com.example.myapplication.model.UserViewModel
import com.example.myapplication.repository.UserRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class EditUserFragment : Fragment() {
    private lateinit var selectedTagsContainer: GridLayout

    private lateinit var addTagButton: Button
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

        // הוספת מאזין לכפתור ההתנתקות
        logoutButton.setOnClickListener {
            logoutUser()
        }

        imagePreview.setOnClickListener {
            openImagePicker()
        }

        // אתחול של ה-ActivityResultLauncher
        selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri = result.data?.data
                imageUri?.let {
                    imagePreview.setImageURI(it)  // הצגת התמונה שנבחרה
                    isImageChanged = true  // סימון שיש שינוי בתמונה
                }
            }
        }
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut() // התנתקות מהמשתמש

        // הסתרת ה-Bottom Navigation
        (activity as? MainActivity)?.hideBottomNavigation()

        // מעבר ל-LoginFragment דרך ה-NavController
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
            // אם יש תמונה חדשה, נעלה אותה ל-Cloudinary
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
