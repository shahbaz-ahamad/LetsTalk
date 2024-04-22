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

        val currentuser = viewmodel.currentUser
        binding.apply {
            if(currentuser?.photoUrl != null){
                Glide
                    .with(this@MainActivity)
                    .load(currentuser?.photoUrl)
                    .into(imageView)

                name.text=currentuser?.displayName
            }else{
                imageView.setImageResource(R.drawable.profile)
                name.text=currentuser?.displayName
            }
        }
    }

}