package com.shahbaz.letstalk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shahbaz.letstalk.repositiory.FirebaseMessagingRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirbaseMessagingViewmodel @Inject constructor(
    private val firebaseMessagingRepo: FirebaseMessagingRepo
):ViewModel() {


    val currentUser= firebaseMessagingRepo.currentUser
    fun updateToken(userId:String,token:String){
        viewModelScope.launch {
            firebaseMessagingRepo.updateToken(userId,token)
        }
    }
}