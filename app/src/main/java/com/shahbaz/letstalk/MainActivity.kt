package com.shahbaz.letstalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.shahbaz.letstalk.databinding.ActivityMainBinding
import com.shahbaz.letstalk.viewmodel.AuthViewmodel
import com.shahbaz.letstalk.viewmodel.UserProfileViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private val viewmodel by viewModels<AuthViewmodel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        displayUserInfo()
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