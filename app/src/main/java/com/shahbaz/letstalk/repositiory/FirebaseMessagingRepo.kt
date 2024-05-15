package com.shahbaz.letstalk.repositiory

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.room.RoomDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class FirebaseMessagingRepo @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase,
    private val roomDao:RoomDao
) {
    val currentUser = firebaseAuth.currentUser

    suspend fun updateToken(userId:String,token:String){
        firebaseDatabase.reference.child("User_Profile")
            .child(userId)
            .child("token")
            .setValue(token)
            .addOnSuccessListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val userProfile = UserProfile(userId = userId, token = token)
                    roomDao.updatetoken(userProfile)
                }
            }
    }
}