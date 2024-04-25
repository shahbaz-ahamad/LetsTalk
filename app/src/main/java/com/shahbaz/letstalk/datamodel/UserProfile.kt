package com.shahbaz.letstalk.datamodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class UserProfile(
    val userId:String ="",
    val userName:String="",
    val userProfileImage:String="",
    val userNumber :String=""
):Parcelable
