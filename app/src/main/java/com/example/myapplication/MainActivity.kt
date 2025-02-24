package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavController
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        FirebaseApp.initializeApp(this)


//        val navHostController: NavHostFragment? = supportFragmentManager.findFragmentById(R.id.main_nav_host) as? NavHostFragment
//        navController = navHostController?.navController
//        navController?.let {
//            NavigationUI.setupActionBarWithNavController(
//                activity = this,
//                navController = it
//            )
//        }

//        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_bar)
//        navController?.let { NavigationUI.setupWithNavController(bottomNavigationView, it) }
    }

}

