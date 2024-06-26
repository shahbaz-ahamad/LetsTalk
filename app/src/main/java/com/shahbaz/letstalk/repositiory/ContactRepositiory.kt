package com.shahbaz.letstalk.repositiory

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shahbaz.letstalk.datamodel.ChatRoomModel
import com.shahbaz.letstalk.datamodel.UnregisteredUser
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.helper.FirebasseUtils
import com.shahbaz.letstalk.sealedclass.Resources
import dagger.hilt.android.qualifiers.ApplicationContext

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Arrays
import javax.inject.Inject

class ContactRepositiory @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    @ApplicationContext private val context: Context,
//    private val roomDao: RoomDao,
    private val firebaseAuth: FirebaseAuth,
    private val firebasseUtils: FirebasseUtils
) {

    private val _registerContactsState =
        MutableStateFlow<Resources<MutableList<UserProfile>>>(Resources.Unspecified())
    val contactState = _registerContactsState.asStateFlow()

    private val _registerContactsStateFromRoomDatabase =
        MutableStateFlow<Resources<MutableList<UserProfile>>>(Resources.Unspecified())
    val registerContactsStateFromRoomDatabase = _registerContactsStateFromRoomDatabase.asStateFlow()


    private val _unRegisterContactState =
        MutableStateFlow<Resources<MutableList<UnregisteredUser>>>(Resources.Unspecified())
    val unRegisterContactState = _unRegisterContactState.asStateFlow()

    var unRegisteredContact = mutableListOf<UnregisteredUser>()

    val currentUser = firebaseAuth.currentUser
    fun fetchContact(): MutableMap<String, String> {
        val contactList = mutableMapOf<String, String>()
        val cursor: Cursor? = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )
        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val phone = it.getString(phoneIndex)
                contactList[name] = phone
            }
        }
        return contactList
    }

    fun fetchRegisterUser(contactList: MutableMap<String, String>) {
        unRegisteredContact = mutableListOf<UnregisteredUser>()
        _registerContactsState.value = Resources.Loading()
        val userList = mutableListOf<UserProfile>()
        val normalizedContactList = contactList.mapValues { it.value.replace("\\s+".toRegex(), "") }
        val userCollectionRef = firebasseUtils.allUserCollectionReference()

        for ((name, phoneNumber) in normalizedContactList) {
            userCollectionRef.whereEqualTo("userNumber", phoneNumber)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val user = document.toObject(UserProfile::class.java)
                        userList.add(user)
                    }
                    if (userList.isNotEmpty()) {
                        _registerContactsState.value = Resources.Success(userList)
                    } else {
                        unRegisteredContact.add(UnregisteredUser(name, phoneNumber))
                        _unRegisterContactState.value = Resources.Success(unRegisteredContact)
                    }
                }
                .addOnFailureListener { exception ->
                    _registerContactsState.value = Resources.Error(exception.message.toString())
                }
        }
    }
}
