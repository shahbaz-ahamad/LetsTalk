package com.shahbaz.letstalk.viewmodel

import androidx.lifecycle.ViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.shahbaz.letstalk.datamodel.ChatRoomModel
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.repositiory.ChatFragmentRepo
import com.shahbaz.letstalk.sealedclass.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class ChatFragmetViewmodel @Inject constructor(
    private val chatFragmentRepo: ChatFragmentRepo
) :ViewModel() {

    val recentChatState :Flow<Resources<FirestoreRecyclerOptions<ChatRoomModel>>> = chatFragmentRepo.recentUserState

    fun recentChat(){
        chatFragmentRepo.fetchRecentChat()
    }

}