package com.shahbaz.letstalk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.shahbaz.letstalk.databinding.ActivityMainBinding
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.fragment.ChatFragmentDirections
import com.shahbaz.letstalk.helper.FirebasseUtils
import com.shahbaz.letstalk.notification.FirebaseMessagingService
import com.shahbaz.letstalk.viewmodel.FirbaseMessagingViewmodel
import com.shahbaz.letstalk.viewmodel.UserProfileViewmodel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val profileViewmodel by viewModels<UserProfileViewmodel>()

    @Inject
    lateinit var firebasseUtils: FirebasseUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setupBottomNavigationView()

        //for the userStauts like online or not
        profileViewmodel.ChangeUserStatus("Online")

        generateTokenAndSavetoFirestore()

        handleNotificationIntent(intent)
    }

    private fun setupBottomNavigationView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }


    override fun onPause() {
        super.onPause()
        setUplastSeen()
    }

    override fun onResume() {
        super.onResume()
        profileViewmodel.ChangeUserStatus("Online")
    }

    override fun onDestroy() {
        super.onDestroy()
        setUplastSeen()
    }


    fun setUplastSeen() {
        // Get the current time in milliseconds
        val currentTimeMillis = System.currentTimeMillis()
        // Convert the current time to a readable date and time format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val lastSeenDateTime = dateFormat.format(Date(currentTimeMillis))
        // Update user status with the current time and date
        profileViewmodel.ChangeUserStatus("Last Seen $lastSeenDateTime")
    }

    fun generateTokenAndSavetoFirestore() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.result
                firebasseUtils.currentUserDetails()?.update("token", token)
                Log.d("token", it.result)
            } else {
                return@addOnCompleteListener
            }
        }


    }

    private fun handleNotificationIntent(intent: Intent) {
        Log.d("intent",intent.toString())
        if (intent.extras != null) {
            val userId = intent.getStringExtra("userId")
            Log.d("UserId",userId.toString())
            if (userId != null) {
                firebasseUtils.allUserCollectionReference().document(userId)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userProfile = task.result.toObject(UserProfile::class.java)
                            userProfile?.let {
                                openChatRoomFragment(userProfile)
                            }
                        }
                    }
            }
        }
    }

    private fun openChatRoomFragment(userProfile: UserProfile) {
        val action = ChatFragmentDirections.actionChatFragmentToChatRoomFragment(userProfile)
        navController.navigate(action)
    }

}


