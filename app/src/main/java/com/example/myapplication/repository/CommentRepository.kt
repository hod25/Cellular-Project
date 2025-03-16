package com.example.myapplication.repository

import android.util.Log
import com.example.myapplication.model.Comment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class CommentRepository {
    private val database = Firebase.firestore
    private val collectionName = "Comments"

    suspend fun getAllComments(): List<Comment> {
        return try {
            val snapshot = database.collection(collectionName).get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Comment::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e("Firebase", "Error fetching Comments", e)
            emptyList()
        }
    }

    suspend fun addComment(comment: Comment): Boolean {
        return try {
            val docRef = database.collection("Comments").add(comment).await()
            docRef.update("id", docRef.id)
            true
        } catch (e: Exception) {
            Log.e("Firebase", "Error adding Comment", e)
            false
        }
    }

    suspend fun updateComment(comment: Comment): Boolean {
        return try {
            database.collection(collectionName).document(comment.id).set(comment).await()
            true
        } catch (e: Exception) {
            Log.e("Firebase", "Error updating Comment", e)
            false
        }
    }

    suspend fun getComment(commentId: String): Map<String, Any>?  {
        return try {
            val document = database.collection("Comments").document(commentId).get().await()
            if (document.exists()) document.data else null
        } catch (e: Exception) {
            Log.e("Firebase", "Error fetching Comment", e)
            null
        }
    }

    suspend fun deleteComment(commentId: String): Boolean {
        return try {
            database.collection(collectionName).document(commentId).delete().await()
            true
        } catch (e: Exception) {
            Log.e("Firebase", "Error deleting Comment", e)
            false
        }
    }

    suspend fun getCommentsForRecipe(recipeId: String): List<Comment> {
        val commentsList = mutableListOf<Comment>()
        try {
            val querySnapshot = database.collection("Comments")
                .whereEqualTo("recipe", recipeId)
                .get()
                .await() // מחכים לשליפה

            for (document in querySnapshot.documents) {
                val comment = document.toObject(Comment::class.java)
                if (comment != null) {
                    commentsList.add(comment)
                }
            }
            Log.d("list",commentsList.toString())
        } catch (e: Exception) {
            Log.e("CommentRepository", "Error fetching comments", e)
        }
        return commentsList
    }
}