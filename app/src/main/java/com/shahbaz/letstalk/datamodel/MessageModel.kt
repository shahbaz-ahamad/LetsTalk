package com.shahbaz.letstalk.datamodel

import com.google.firebase.Timestamp


data class MessageModel(
    val senderId: String, // ID of the user who sent the message
    val message: String, // The text of the message
    val timestamp: Timestamp, // The timestamp when the message was sent
    val isRead: Boolean, //to check whether the user has gone through message or not
){
    constructor() : this("", "", Timestamp.now(), false)
}
