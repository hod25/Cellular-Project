package com.example.myapplication

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

        FirebaseApp.initializeApp(this)

        val config = mapOf(
            "cloud_name" to BuildConfig.CLOUD_NAME,
            "api_key" to BuildConfig.API_KEY,
            "api_secret" to BuildConfig.API_SECRET
        )
        MediaManager.init(this, config)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment

        if (navHostFragment != null) {
            navController = navHostFragment.navController

            bottomNavigationView = findViewById(R.id.bottom_bar)
            NavigationUI.setupWithNavController(bottomNavigationView, navController)

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                navController.navigate(R.id.feedFragment)
            } else {
                navController.navigate(R.id.loginFragment)
            }

            if (currentUser == null) {
                hideBottomNavigation()
            } else {
                showBottomNavigation()
            }
        } else {
            println("⚠️ Error: nav_host_fragment not found!")
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

    fun showBottomNavigation() {
        bottomNavigationView.visibility = View.VISIBLE
    }

    fun hideBottomNavigation() {
        bottomNavigationView.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
