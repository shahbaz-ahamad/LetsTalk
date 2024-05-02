package com.shahbaz.letstalk.repositiory

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.sealedclass.Resources
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ChatFragmentRepo @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) {

    private val _recentuserSate = MutableStateFlow<Resources<MutableList<UserProfile>>>(Resources.Unspecified())
    val recentUserState = _recentuserSate.asStateFlow()
    fun fetchRecentUser(){
        val userList = mutableListOf<UserProfile>()
        firebaseDatabase.reference
            .child("User_Profile")
            .orderByChild("recent")
            .equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (snapshots in snapshot.children){
                        val user = snapshots.getValue(UserProfile::class.java)
                        if(user != null){
                            userList.add(user)
                        }
                    }

                    if(userList.isNotEmpty()){
                        _recentuserSate.value=Resources.Success(userList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _recentuserSate.value=Resources.Error(error.message.toString())
                }

            })
    }
}