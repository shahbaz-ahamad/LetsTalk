package com.shahbaz.letstalk.sealedclass

import com.shahbaz.letstalk.datamodel.UserProfile

sealed class UserProfileSetupState{
    object Loading :UserProfileSetupState()
    object Unspecified :UserProfileSetupState()
    data class Success(val user:UserProfile):UserProfileSetupState()
    data class Error(val errorMsg :String) :UserProfileSetupState()
}
