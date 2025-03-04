package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import com.cloudinary.android.MediaManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // אתחול Firebase
        FirebaseApp.initializeApp(this)

        // אתחול Cloudinary
        val config = mapOf(
            "cloud_name" to BuildConfig.CLOUD_NAME,
            "api_key" to BuildConfig.API_KEY,
            "api_secret" to BuildConfig.API_SECRET
        )
        MediaManager.init(this, config)

        // מציאת ה-NavHostFragment בתוך ה-XML
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment

        if (navHostFragment != null) {
            navController = navHostFragment.navController

            // חיבור ה-Bottom Navigation ל-NavController
            bottomNavigationView = findViewById(R.id.bottom_bar)
            NavigationUI.setupWithNavController(bottomNavigationView, navController)

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                navController.navigate(R.id.feedFragment)
            } else {
                navController.navigate(R.id.loginFragment)
            }

            // הצגת/הסתרת ה-Bottom Navigation בהתאם למשתמש
            if (currentUser == null) {
                hideBottomNavigation()
            } else {
                showBottomNavigation()
            }
        } else {
            // הדפסת שגיאה ללוג במקרה שאין nav_host_fragment
            println("⚠️ Error: nav_host_fragment not found!")
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment) // ודא שיש לך `fragment_container` ב- XML
            .commit()
    }

    // הפוך את הפונקציה ל-public
    fun showBottomNavigation() {
        bottomNavigationView.visibility = View.VISIBLE
    }

    fun hideBottomNavigation() {
        bottomNavigationView.visibility = View.GONE
    }

    // מאזין ללחיצות על הכפתורים בתפריט
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
