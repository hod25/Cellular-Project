package com.example.myapplication.model

data class Comment(val recipe : String,val content: String, val imageResId: Int) {

    // בנאי נוסף שמתאים לחתימה שצוינה בשגיאה
    constructor(content: String, imageResId: Int) : this("", content, imageResId)
}
