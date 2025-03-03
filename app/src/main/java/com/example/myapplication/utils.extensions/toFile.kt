package com.example.myapplication.utils.extensions

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

fun Bitmap.toFile(context: Context, fileName: String): File {
    // יצירת תיקייה לשמירת התמונות
    val dir = File(context.cacheDir, "images").apply { mkdirs() }

    // יצירת קובץ עם השם שהועבר לפונקציה
    val file = File(dir, "$fileName.jpg")

    // שמירת ה-Bitmap בקובץ
    FileOutputStream(file).use { outputStream ->
        this.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
    }

    return file
}
