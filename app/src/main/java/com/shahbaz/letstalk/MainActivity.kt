package com.shahbaz.letstalk

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.shahbaz.letstalk.databinding.ActivityMainBinding
import com.shahbaz.letstalk.viewmodel.AuthViewmodel
import com.shahbaz.letstalk.viewmodel.UserProfileViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var navController: NavController
    private val viewmodel by viewModels<AuthViewmodel>()
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        displayUserInfo()
        setupBottomNavigationView()
    }

    private fun setupBottomNavigationView() {
        val navHostFragment= supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        navController=navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }
    private fun displayUserInfo() {

        val currentUser = viewmodel.currentUser
        binding.apply {

            if(currentUser?.photoUrl != null){
                Glide
                    .with(this@MainActivity)
                    .load(currentUser?.photoUrl)
                    .placeholder(R.drawable.profile)
                    .into(profileImage)

                name.text= currentUser?.displayName
            }else{
                profileImage.setImageResource(R.drawable.profile)
                name.text=currentUser?.displayName
            }
        }
    }
}