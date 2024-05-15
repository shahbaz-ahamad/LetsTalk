package com.shahbaz.letstalk.repositiory

import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.Query
import com.shahbaz.letstalk.datamodel.ChatRoomModel
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.helper.FirebasseUtils
import com.shahbaz.letstalk.sealedclass.Resources
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ChatFragmentRepo @Inject constructor(
    private val firebasseUtils: FirebasseUtils
) {

    private val _recentuserSate = MutableStateFlow<Resources<FirestoreRecyclerOptions<ChatRoomModel>>>(Resources.Unspecified())
    val recentUserState = _recentuserSate.asStateFlow()


    fun fetchRecentChat(){
        try {
            _recentuserSate.value=Resources.Loading()
            val query = firebasseUtils.currentUserId()?.let {currentUserId ->
                firebasseUtils.allChatroomCollectionReference()
                    .whereArrayContains("userId", currentUserId)
                    .orderBy("lastMessagetimeStamp",Query.Direction.DESCENDING)
            }

            query?.let {safeQury ->
                val options= FirestoreRecyclerOptions.Builder<ChatRoomModel>()
                    .setQuery(safeQury,ChatRoomModel::class.java)
                    .build()
                _recentuserSate.value= Resources.Success(options)
            } ?: kotlin.run {
                _recentuserSate.value = Resources.Error("User ID is null")
            }
        }catch (e :Exception){
            _recentuserSate.value=Resources.Error(e.localizedMessage.toString())
        }
    }
}