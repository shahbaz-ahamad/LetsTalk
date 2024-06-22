package com.shahbaz.letstalk.repositiory

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.FirebaseStorage
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.helper.FirebasseUtils
import com.shahbaz.letstalk.sealedclass.Resources
import com.shahbaz.letstalk.sealedclass.UserProfileSetupState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


class ProfileRepo @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseUtil: FirebasseUtils,
    ) {

    private val _userUpdateStatus =
        MutableStateFlow<UserProfileSetupState>(UserProfileSetupState.Unspecified)
    val userUpdateStatus = _userUpdateStatus.asStateFlow()



    private val _userOnlineStatus =MutableStateFlow<Resources<String>>(Resources.Unspecified())
    val userOnlineStatus =_userOnlineStatus.asStateFlow()

    val currentUser
        get() =
            firebaseAuth.currentUser


    fun AddUserDataToFirebase(name: String, selectedImageUri: Uri?) {
        _userUpdateStatus.value = UserProfileSetupState.Loading
        if (selectedImageUri != null) {
            val imageName = currentUser?.uid + name
            val imagereference = firebaseStorage.reference.child("ProfileImage/$imageName")
            imagereference.putFile(selectedImageUri)
                .addOnSuccessListener { taskSnapshot ->
                    imagereference.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri
                        //store the user profile now

                        currentUser?.let {
                            val profile = userProfileChangeRequest {
                                displayName = name
                                photoUri = imageUrl
                            }

                            it.updateProfile(profile)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        val id = currentUser!!.uid
                                        val userProfile = UserProfile(
                                            id,
                                            name,
                                            imageUrl.toString(),
                                            currentUser!!.phoneNumber.toString(),
                                        )
                                       // AddUserToRealtimeDatabase(userProfile)
                                        AddUserToFirestore(userProfile)
                                    } else {
                                        _userUpdateStatus.value =
                                            UserProfileSetupState.Error("failed to update")

                                    }
                                }
                        }


                    }
                        .addOnFailureListener {
                            //failed to dowload image url
                            _userUpdateStatus.value =
                                UserProfileSetupState.Error(it.localizedMessage)
                        }
                }
                .addOnFailureListener {
                    //failed to upload image
                    _userUpdateStatus.value = UserProfileSetupState.Error(it.localizedMessage)
                }
        } else {
            //store the user profile now
            currentUser?.let {
                val profile = userProfileChangeRequest {
                    displayName = name
                }
                it.updateProfile(profile)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val id = currentUser!!.uid
                            val userProfile = UserProfile(
                                id,
                                name,
                                "",
                                currentUser!!.phoneNumber.toString()
                            )
                           // AddUserToRealtimeDatabase(userProfile)
                            AddUserToFirestore(userProfile)
                        } else {
                            _userUpdateStatus.value =
                                UserProfileSetupState.Error("failed to update")

                        }
                    }
            }
        }
    }

    fun AddUserToFirestore(userProfile: UserProfile) {
        firebaseUtil.currentUserDetails()?.set(userProfile)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    _userUpdateStatus.value = UserProfileSetupState.Success(userProfile)
                }
            }
            ?.addOnFailureListener {
                _userUpdateStatus.value = UserProfileSetupState.Error(it.localizedMessage)
            }
    }


    fun ChangeUserStatus(status:String){
        val userStatus = hashMapOf<String,Any>(
            "recent" to status
        )
        firebaseUtil.currentUserId()?.let {
            firebaseUtil.allUserCollectionReference().document(it)
                .update(userStatus)
                .addOnSuccessListener {

                }
                .addOnFailureListener {

                }
        }
    }


    fun fetchUserStatus(userId: String) {
        firebaseUtil.allUserCollectionReference().document(userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    _userOnlineStatus.value = Resources.Error("Failed to fetch status: ${exception.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val result = snapshot.getString("recent")
                    _userOnlineStatus.value = Resources.Success(result.toString())
                } else {
                    _userOnlineStatus.value = Resources.Error("User document doesn't exist")
                }
            }
    }

}