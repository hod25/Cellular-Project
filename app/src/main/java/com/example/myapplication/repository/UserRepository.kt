package com.example.myapplication.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // פונקציה להתחברות עם אימייל וסיסמא
    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            // שגיאה בהתחברות
            false
        }
    }

    // אפשר להוסיף פונקציה לשחזור סיסמא אם צריך
    suspend fun resetPassword(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // 1️⃣ רישום המשתמש ב-Firebase Authentication
    suspend fun registerUser(email: String, password: String): Boolean {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.user != null  // מחזיר true אם ההרשמה הצליחה
        } catch (e: Exception) {
            Log.e("UserRepository", "Registration failed: ${e.localizedMessage}")
            false
        }
    }

    // 2️⃣ שמירת פרטי המשתמש ב-Firestore
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
}
