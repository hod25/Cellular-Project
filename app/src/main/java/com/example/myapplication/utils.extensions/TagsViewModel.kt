package com.example.myapplication.utils.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TagsViewModel : ViewModel() {
    private val _tags = MutableLiveData<List<String>>(emptyList())
    val tags: LiveData<List<String>> = _tags

    fun addTag(ingredient: String) {
        _tags.value = _tags.value?.toMutableList()?.apply { add(ingredient) }
    }

    fun removeTag(ingredient: String) {
        _tags.value = _tags.value?.toMutableList()?.apply { remove(ingredient) }
    }

    fun setTags(tags: List<String>) {
        _tags.value = tags
    }

    fun removeAllTags() {
        _tags.value = emptyList()
    }
}