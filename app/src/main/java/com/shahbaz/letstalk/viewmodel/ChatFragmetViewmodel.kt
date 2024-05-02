package com.shahbaz.letstalk.viewmodel

import androidx.lifecycle.ViewModel
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

    val chatFragmentrepoState :Flow<Resources<MutableList<UserProfile>>> = chatFragmentRepo.recentUserState
    
    fun recentUser(){
        chatFragmentRepo.fetchRecentUser()
    }
}