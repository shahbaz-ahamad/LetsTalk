package com.shahbaz.letstalk.repositiory

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shahbaz.letstalk.datamodel.MessageModel
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.sealedclass.Resources
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

class ChatRepo @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) {


    private val _messageSendState =
        MutableStateFlow<Resources<List<MessageModel>>>(Resources.Unspecified())
    val messageSateSend = _messageSendState.asStateFlow()


    private val _messageFetched =
        MutableStateFlow<Resources<ArrayList<MessageModel>>>(Resources.Unspecified())
    val messageFetched = _messageFetched.asStateFlow()

    fun sendMessage(user: UserProfile, message: String) {
        var senderUid = firebaseAuth.currentUser?.uid.toString()
        var receiverUid = user.userId
        var senderRoom = senderUid + receiverUid
        var receiverRoom = receiverUid + senderUid
        val messageModel = MessageModel(senderUid, message, System.currentTimeMillis(), false, user)
        storeMessageInRealtimeDatabase(senderRoom, receiverRoom, messageModel)
    }

    fun generateRandomId(): String {
        return UUID.randomUUID().toString()
    }

    fun storeMessageInRealtimeDatabase(
        senderRoom: String,
        receiverRoom: String,
        message: MessageModel
    ) {
        val senderRef = firebaseDatabase.reference
            .child("Chats")
            .child(senderRoom)
            .child("Message")
            .push()
        val receiverRef = firebaseDatabase.reference
            .child("Chats")
            .child(receiverRoom)
            .child("Message")
            .push()

        senderRef.setValue(message)
            .addOnSuccessListener {
                receiverRef.setValue(message)
                    .addOnSuccessListener {
                        _messageSendState.value = Resources.Success(listOf(message))
                    }
                    .addOnFailureListener {
                        _messageSendState.value = Resources.Error("Can't Insert to Receiver")
                    }
            }.addOnFailureListener {
                _messageSendState.value = Resources.Error("Unable to send Message")
            }

    }


    fun fetchMessage(user: UserProfile) {
        val message=ArrayList<MessageModel>()
        val senderRoom=firebaseAuth.currentUser?.uid+user.userId
        firebaseDatabase.reference
            .child("Chats")
            .child(senderRoom)
            .child("Message")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    message.clear()
                    for (snapshots in snapshot.children) {
                        val data =snapshots.getValue(MessageModel::class.java)
                        message.add(data!!)
                        if(message.isNotEmpty()){
                            _messageFetched.value=Resources.Success(message)
                        }else{
                            _messageFetched.value=Resources.Error("No Message Found")
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    _messageFetched.value=Resources.Error(error.message.toString())
                }

            })
    }

}