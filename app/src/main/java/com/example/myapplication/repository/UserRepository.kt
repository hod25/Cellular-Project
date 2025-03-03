package com.example.myapplication.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

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
}
