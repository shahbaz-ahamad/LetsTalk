package com.shahbaz.letstalk.datamodel

import java.util.Date



data class MessageModel(
    val senderId: String, // ID of the user who sent the message
    val message: String, // The text of the message
    val timestamp: Long, // The timestamp when the message was sent
    val isRead: Boolean, //to check whether the user has gone through message or not
    val userProfile: UserProfile
){
    constructor() : this("", "", 0, false, UserProfile())
}
