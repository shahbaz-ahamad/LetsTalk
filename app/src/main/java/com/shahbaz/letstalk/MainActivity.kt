package com.shahbaz.letstalk

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessaging
import com.shahbaz.letstalk.databinding.ActivityMainBinding
import com.shahbaz.letstalk.firebasemessaging.FirebaseMessagingService
import com.shahbaz.letstalk.viewmodel.AuthViewmodel
import com.shahbaz.letstalk.viewmodel.FirbaseMessagingViewmodel
import com.shahbaz.letstalk.viewmodel.UserProfileViewmodel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewmodel by viewModels<FirbaseMessagingViewmodel>()
    private val profileViewmodel by viewModels<UserProfileViewmodel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setupBottomNavigationView()

        //for the userStauts like online or not
        profileViewmodel.ChangeUserStatus("Online")

    }

    private fun setupBottomNavigationView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }


    override fun onPause() {
        super.onPause()
        // Get the current time in milliseconds
        val currentTimeMillis = System.currentTimeMillis()
        // Convert the current time to a readable date and time format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val lastSeenDateTime = dateFormat.format(Date(currentTimeMillis))
        // Update user status with the current time and date
        profileViewmodel.ChangeUserStatus("Last Seen $lastSeenDateTime")
    }

    override fun onResume() {
        super.onResume()
        profileViewmodel.ChangeUserStatus("Online")
    }

}