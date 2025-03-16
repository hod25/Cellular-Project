package com.example.myapplication.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Comment
import com.example.myapplication.repository.CommentRepository
import kotlinx.coroutines.launch

class CommentViewModel : ViewModel() {
    private val repository = CommentRepository()

    // MutableLiveData to update the UI
    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> get() = _comments

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // Get all comments from the repository
    fun getAllComments() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val commentsList = repository.getAllComments()
                _comments.value = commentsList
            } catch (e: Exception) {
                _error.value = "Failed to load comments"
                Log.e("CommentViewModel", "Error fetching comments", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Add a new comment
    fun addComment(comment: Comment) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val isSuccess = repository.addComment(comment)
                if (isSuccess) {
                    getAllComments() // Refresh the comments after adding
                } else {
                    _error.value = "Failed to add comment"
                }
            } catch (e: Exception) {
                _error.value = "Error adding comment"
                Log.e("CommentViewModel", "Error adding comment", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Update an existing comment
    fun updateComment(comment: Comment) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val isSuccess = repository.updateComment(comment)
                if (isSuccess) {
                    getAllComments() // Refresh the comments after updating
                } else {
                    _error.value = "Failed to update comment"
                }
            } catch (e: Exception) {
                _error.value = "Error updating comment"
                Log.e("CommentViewModel", "Error updating comment", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Get a single comment by ID
    fun getComment(commentId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val comment = repository.getComment(commentId)
                if (comment != null) {
                    // Handle the retrieved comment, update UI if necessary
                } else {
                    _error.value = "Comment not found"
                }
            } catch (e: Exception) {
                _error.value = "Error fetching comment"
                Log.e("CommentViewModel", "Error fetching comment", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
