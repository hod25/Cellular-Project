package com.example.myapplication.repository

import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class UserRepository {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // פונקציה להתחברות עם אימייל וסיסמה
    suspend fun loginUser(email: String, password: String): String? {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.user?.uid // מחזיר את ה-UID של המשתמש
        } catch (e: Exception) {
            Log.e("UserRepository", "Login failed: ${e.localizedMessage}")
            null
        }
    }


    // פונקציה לשחזור סיסמה
    suspend fun resetPassword(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Reset password failed: ${e.localizedMessage}")
            false
        }
    }

    // רישום משתמש חדש
    suspend fun registerUser(email: String, password: String): Boolean {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.user != null  // מחזיר true אם ההרשמה הצליחה
        } catch (e: Exception) {
            Log.e("UserRepository", "Registration failed: ${e.localizedMessage}")
            false
        }
    }

    // שמירת פרטי המשתמש ב-Firestore
    suspend fun saveUserData(uid: String, firstName: String, lastName: String, email: String): Boolean {
        return try {
            val user = hashMapOf(
                "firstName" to firstName,
                "lastName" to lastName,
                "email" to email
            )
            db.collection("users").document(uid).set(user).await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error saving user data: ${e.localizedMessage}")
            false
        }
    }

    // פונקציה לקבלת נתוני המשתמש מ-Firestore
    suspend fun getUserData(uid: String): Map<String, Any>? {
        return try {
            val document = db.collection("users").document(uid).get().await()
            if (document.exists()) document.data else null
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user data: ${e.localizedMessage}")
            null
        }
    }

    // פונקציה לעדכון פרטי המשתמש ב-Firestore
    suspend fun updateUserData(uid: String, firstName: String, lastName: String, password: String): Boolean {
        return try {
            // עדכון נתונים ב-Firestore
            val user = hashMapOf(
                "firstName" to firstName,
                "lastName" to lastName
            )
            db.collection("users").document(uid).update(user as Map<String, Any>).await()

            // עדכון סיסמא ב-FirebaseAuth
            val userFirebase = auth.currentUser
            userFirebase?.updatePassword(password)?.await()

            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating user data: ${e.localizedMessage}")
            false
        }
    }

//    suspend fun saveUserImage(uid: String, imageUrl: String): Boolean {
//        return try {
//            val userRef = FirebaseFirestore.getInstance().collection("users").document(uid)
//            userRef.update("profileImageUrl", imageUrl).await()
//            true
//        } catch (e: Exception) {
//            false
//        }
//    }

    suspend fun uploadImageToCloudinary(imageUri: Uri): String? {
        return suspendCancellableCoroutine { continuation ->
            MediaManager.get().upload(imageUri)
                .callback(object : UploadCallback {
                    override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                        val imageUrl = resultData?.get("secure_url") as? String
                        continuation.resume(imageUrl)
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        continuation.resume(null)
                    }

                    override fun onStart(requestId: String?) {}
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                })
                .dispatch()
        }
    }

    suspend fun saveUserImage(userId: String, imageUrl: String): Boolean {
        return try {
            db.collection("users")
                .document(userId)
                .update("profileImageUrl", imageUrl)
                .await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error saving user image: ${e.localizedMessage}")
            false
        }
    }
}
