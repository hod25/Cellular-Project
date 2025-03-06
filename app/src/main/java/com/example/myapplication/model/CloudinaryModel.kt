package com.example.myapplication.model

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.callback.ErrorInfo
//import com.cloudinary.android.policy.GlobalUploadPolicy
//import com.example.myapplication.base.myapplication
import com.example.myapplication.BuildConfig
import com.example.myapplication.repository.RecipeRepository
import com.example.myapplication.utils.extensions.toFile
import kotlinx.coroutines.launch
import java.io.File

import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.UploadCallback
import android.net.Uri
import android.widget.Toast
import com.idz.myapplication.base.MyApplication.Globals.context

public fun uploadImage(uri: Uri) {
    // העלאת התמונה ל-Cloudinary
    val request = MediaManager.get().upload(uri)
        .callback(object : UploadCallback {
            override fun onStart(requestId: String?) {
                // קרה כשההעלאה התחילה
            }

            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                // חישוב התקדמות ההעלאה
                val progress = (bytes * 100) / totalBytes
                println("Upload Progress: $progress%")
            }

            override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                // העלאה הצליחה
                // התמונה הועלתה בהצלחה ל-Cloudinary
                val imageUrl = resultData?.get("url") as String?
                Toast.makeText(context, "Image uploaded successfully: $imageUrl", Toast.LENGTH_SHORT).show()
                // כאן תוכל לשמור את ה-URL של התמונה ב-DB שלך
            }

            override fun onError(requestId: String?, error: ErrorInfo?) {
                // קרה כשיש שגיאה בהעלאה
                Toast.makeText(context, "Error uploading image", Toast.LENGTH_SHORT).show()
            }

            override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                // קרה אם ההעלאה נדחתה
            }
        })
}
