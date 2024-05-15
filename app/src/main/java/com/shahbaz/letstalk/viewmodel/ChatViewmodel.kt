package com.shahbaz.letstalk.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.shahbaz.letstalk.datamodel.MessageModel
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.fragment.ChatRoomFragment
import com.shahbaz.letstalk.repositiory.ChatRepo
import com.shahbaz.letstalk.sealedclass.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class ChatViewmodel @Inject constructor(
    private val chatRepo: ChatRepo,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {


    val messageOptions: FirestoreRecyclerOptions<MessageModel> =
        chatRepo.getMessageOptions(ChatRoomFragment.chatRoomId)

    val currentUser = firebaseAuth.currentUser

    fun sendMessage(chatRoomId: String,message: String) {
        chatRepo.sendMessage(chatRoomId,message)
    }


    fun getOrCreateChatRoomModel(chatRoomId:String,userProfile: UserProfile) {
        chatRepo.getOrCreateChatRoom(chatRoomId,userProfile)
    }
}