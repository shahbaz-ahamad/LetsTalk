package com.shahbaz.letstalk.repositiory

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.sealedclass.UserProfileSetupState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


class ProfileRepo @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseStorage: FirebaseStorage
) {

    private val _userUpdateStatus =
        MutableStateFlow<UserProfileSetupState>(UserProfileSetupState.Unspecified)
    val userUpdateStatus = _userUpdateStatus.asStateFlow()

    val currentUser
        get() =
            firebaseAuth.currentUser

    fun AddUserDataToFirebase(name: String, selectedImageUri: Uri?) {

        Log.d("uid", currentUser?.uid.toString())
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
                                            currentUser!!.phoneNumber.toString()
                                        )
                                        AddUserToRealtimeDatabase(userProfile)
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
                            AddUserToRealtimeDatabase(userProfile)
                        } else {
                            _userUpdateStatus.value =
                                UserProfileSetupState.Error("failed to update")

                        }
                    }
            }
        }
    }


    fun AddUserToRealtimeDatabase(userProfile: UserProfile) {
        val storageRef = currentUser?.let {
            firebaseDatabase.reference.child("User_Profile").child(
                it.uid
            )
        }

        storageRef?.setValue(userProfile)
            ?.addOnSuccessListener {
                _userUpdateStatus.value = UserProfileSetupState.Success(userProfile)
            }
            ?.addOnFailureListener {
                _userUpdateStatus.value = UserProfileSetupState.Error(it.localizedMessage)
            }
    }
}