package com.shahbaz.letstalk.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shahbaz.letstalk.datamodel.UnregisteredUser
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.repositiory.ContactRepositiory
import com.shahbaz.letstalk.sealedclass.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class ContactViewmodel @Inject constructor(
    private val contactRepositiory: ContactRepositiory
) :ViewModel() {
    val registerContactsState : Flow<Resources<MutableList<UserProfile>>> = contactRepositiory.contactState
    val registerContactsFromRoomState : Flow<Resources<MutableList<UserProfile>>> = contactRepositiory.registerContactsStateFromRoomDatabase
    val unRegisterContactState:Flow<Resources<MutableList<UnregisteredUser>>> =contactRepositiory.unRegisterContactState
    fun FetchContact() :MutableMap<String,String>{
       return contactRepositiory.fetchContact()
    }

    fun FetchRegisterUser(contactList : MutableMap<String,String>){
        contactRepositiory.fetchRegisterUserAndInserItToRoomDatabase(contactList)
    }

    fun FetchRegisterUserFromRooDatabase(){
        contactRepositiory.fetchUserFromRoomDatabase()
    }

    val currentUser = contactRepositiory.currentUser

}