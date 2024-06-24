package com.shahbaz.letstalk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.shahbaz.letstalk.databinding.ActivityLoginBinding
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.fragment.ChatFragmentDirections
import com.shahbaz.letstalk.viewmodel.AuthViewmodel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    private lateinit var navController: NavController
    private val viemodel by viewModels<AuthViewmodel>()

    companion object{
        var VERFICATIONID :String?=""
    }


    override fun onStart() {
        super.onStart()

        val currentUser = viemodel.currentUser
        if (currentUser!= null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigationGraph()
    }

    private fun setupNavigationGraph() {
        val navHostFragment=supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController=navHostFragment.navController
    }



}