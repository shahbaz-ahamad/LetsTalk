package com.shahbaz.letstalk.datamodel

import com.google.firebase.Timestamp

data class ChatRoomModel(
    val chatRoomId:String ="",
    val userId:List<String> = listOf(),
    var lastMessagetimeStamp : Timestamp=Timestamp.now(),
    var lastMessageSenderId :String="",
    var lastMessage:String =""
){
    constructor() : this("", listOf(), Timestamp.now(),"","")
}
