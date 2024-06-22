package com.shahbaz.letstalk.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.repositiory.ProfileRepo
import com.shahbaz.letstalk.sealedclass.PhoneAuthState
import com.shahbaz.letstalk.sealedclass.Resources
import com.shahbaz.letstalk.sealedclass.UserProfileSetupState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class UserProfileViewmodel @Inject constructor(
    private val profileRepo :ProfileRepo
) :ViewModel() {

    val profileUpdateState : Flow<UserProfileSetupState> =profileRepo.userUpdateStatus
    val userOnlineStatus : Flow<Resources<String>> =profileRepo.userOnlineStatus

    fun addDataToDatabase(name:String , imageUri: Uri?){
        profileRepo.AddUserDataToFirebase(name,imageUri)
    }

    fun ChangeUserStatus(status:String){
        profileRepo.ChangeUserStatus(status)
    }

    fun FetchUserOnlineStatus(userId:String){
        profileRepo.fetchUserStatus(userId)
    }
}