package com.shahbaz.letstalk.datamodel

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize


@Parcelize
data class UserProfile(
    val userId:String ="",
    val userName:String="",
    val userProfileImage:String="",
    val userNumber :String="",
    val recent:String="",
    val token:String="",
):Parcelable{
    constructor():this("","","","","","")
}
