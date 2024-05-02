package com.shahbaz.letstalk.datamodel

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "UserProfile")
data class UserProfile(
    @PrimaryKey
    val userId:String ="",
    val userName:String="",
    val userProfileImage:String="",
    val userNumber :String="",
    val recent:Boolean=false
):Parcelable
