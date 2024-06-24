package com.shahbaz.letstalk.repositiory

import android.util.Log
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.shahbaz.letstalk.datamodel.ChatRoomModel
import com.shahbaz.letstalk.datamodel.MessageModel
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.helper.FirebasseUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject
import com.shahbaz.letstalk.helper.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatRepo @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebasseUtils: FirebasseUtils
) {
    val currentUser = firebaseAuth.currentUser
    var chatRoomModel = ChatRoomModel()

    fun getOrCreateChatRoom(chatRoomId: String, userProfile: UserProfile) {
        firebasseUtils.getChatRoomReference(chatRoomId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result.toObject(ChatRoomModel::class.java)
                    if (result == null) {
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

    fun sendMessage(chatRoomId: String, message: String, token: String) {
        chatRoomModel.lastMessagetimeStamp = Timestamp.now()
        chatRoomModel.lastMessageSenderId = firebasseUtils.currentUserId().toString()
        chatRoomModel.lastMessage = message
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
                            Log.d("Message", "Message Sent")
                            CoroutineScope(Dispatchers.IO).launch {
                                sendNotification(message,token)
                            }
                        }
                }
            }.addOnFailureListener {
                Log.d("Error", "Failed to update Chatroom")
            }
    }

    private fun sendNotification(message: String,receiverToken:String) {
        val json = JSONObject()
        val dataJson = JSONObject()
        dataJson.put(
            "title",firebasseUtils.currentUserUsername()
        )
        dataJson.put("body", message)
        json.put("to", receiverToken)
        json.put("notification", dataJson)
        json.put("userId",firebasseUtils.currentUserId())

        val body =
            json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://fcm.googleapis.com/fcm/send")
            .post(body)
            .addHeader(
                "Authorization",
                "key=${Constant.SERVER_KEY}"
            ) // Replace YOUR_SERVER_KEY with your actual server key from Firebase Console
            .addHeader("Content-Type", "application/json")
            .build()
        val client = OkHttpClient()
        client.newCall(request).execute()
    }

    fun getMessageOptions(chatRoomId: String): FirestoreRecyclerOptions<MessageModel> {
        val query: Query = firebasseUtils.getChatRoomMessageReference(chatRoomId)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        return FirestoreRecyclerOptions.Builder<MessageModel>()
            .setQuery(query, MessageModel::class.java)
            .build()
    }

}