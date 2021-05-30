package com.juniortech.coviddetector.ui.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.juniortech.coviddetector.R
import com.juniortech.coviddetector.databinding.ActivityMainBinding
import com.juniortech.coviddetector.ui.authentication.LoginActivity
import com.juniortech.coviddetector.ui.profile.ProfileViewModel
import com.juniortech.coviddetector.viewmodel.ViewModelFactory
import com.juniortech.coviddetector.vo.Status

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment)
        val fAuth = FirebaseAuth.getInstance()

        navView.setupWithNavController(navController)

        binding.mainToolbar.setOnMenuItemClickListener {
            if (it.itemId.equals(R.id.action_logout)){
                fAuth.signOut()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
            true
        }
    }
}