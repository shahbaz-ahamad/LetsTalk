package com.shahbaz.letstalk.repositiory

import android.util.Log
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.shahbaz.letstalk.datamodel.ChatRoomModel
import com.shahbaz.letstalk.datamodel.MessageModel
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.helper.FirebasseUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.Arrays
import javax.inject.Inject


class ChatRepo @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebasseUtils: FirebasseUtils
) {
    val currentUser = firebaseAuth.currentUser
    var chatRoomModel = ChatRoomModel()

    fun getOrCreateChatRoom(chatRoomId: String, userProfile: UserProfile) {
        firebasseUtils.getChatRoomReference(chatRoomId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val result = task.result.toObject(ChatRoomModel::class.java)
                    if(result == null){
                        chatRoomModel = ChatRoomModel(
                            chatRoomId,
                            listOf(currentUser?.uid.toString(), userProfile.userId),
                            Timestamp.now(),
                            "",
                            ""
                        )
                        firebasseUtils.getChatRoomReference(chatRoomId).set(chatRoomModel)
                    }
                }
            }
    }

    fun sendMessage(chatRoomId: String, message: String) {
        chatRoomModel.lastMessagetimeStamp = Timestamp.now()
        chatRoomModel.lastMessageSenderId = firebasseUtils.currentUserId().toString()
        chatRoomModel.lastMessage=message
        val messageModel =
            firebasseUtils.currentUserId()
                ?.let { MessageModel(it, message, Timestamp.now(), false) }

        firebasseUtils.getChatRoomReference(chatRoomId)
            .update(
                mapOf(
                    "lastMessagetimeStamp" to chatRoomModel.lastMessagetimeStamp,
                    "lastMessageSenderId" to chatRoomModel.lastMessageSenderId,
                    "lastMessage" to chatRoomModel.lastMessage
                )
            ).addOnCompleteListener {
                // After updating the chat room, add the message to the messages collection
                if (messageModel != null) {
                    firebasseUtils.getChatRoomMessageReference(chatRoomId)
                        .add(messageModel)
                        .addOnCompleteListener {
                            Log.d("Message", "Message Stored")
                        }
                }
            }.addOnFailureListener {
                Log.d("Error", "Failed to update Chatroom")
            }


    }

    fun getMessageOptions(chatRoomId: String): FirestoreRecyclerOptions<MessageModel> {
        val query: Query = firebasseUtils.getChatRoomMessageReference(chatRoomId)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        return FirestoreRecyclerOptions.Builder<MessageModel>()
            .setQuery(query, MessageModel::class.java)
            .build()
    }

}