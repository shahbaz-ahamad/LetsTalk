package com.shahbaz.letstalk.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.shahbaz.letstalk.datamodel.MessageModel
import com.shahbaz.letstalk.datamodel.UserProfile
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

    val chatSendState : Flow<Resources<List<MessageModel>>> =chatRepo.messageSateSend
    val chatFetched : Flow<Resources<ArrayList<MessageModel>>>  = chatRepo.messageFetched


    val currentUser = firebaseAuth.currentUser

    fun sendMessage(user:UserProfile,message: String){
       chatRepo.sendMessage(user,message)
    }

    fun fetchedMesage(user: UserProfile){
        chatRepo.fetchMessage(user)
    }
}