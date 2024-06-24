package com.shahbaz.letstalk.helper

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirebasseUtils @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firbaseAuth:FirebaseAuth
){

    fun currentUserId():String?{
        return firbaseAuth.currentUser?.uid
    }

    fun currentUserUsername():String?{
        return firbaseAuth.currentUser?.displayName
    }
    fun currentUserDetails():DocumentReference?{
        return currentUserId()?.let { firestore.collection("User").document(it) }
    }

    fun allUserCollectionReference():CollectionReference{
        return firestore.collection("User")
    }

    fun getChatRoomReference(chatRoomId:String):DocumentReference{
        return firestore.collection("ChatRooms").document(chatRoomId)
    }

    fun getChatRoomId(user1:String,user2:String):String{
        if(user1.hashCode() < user2.hashCode()){
            return user1+"_"+user2
        }else{
            return user2+"_"+user1
        }
    }

    fun getChatRoomMessageReference(chatRoomId: String):CollectionReference{
        return getChatRoomReference(chatRoomId).collection("Chats")
    }


    fun allChatroomCollectionReference():CollectionReference{
        return firestore.collection("ChatRooms")
    }

    fun getOtherUserFromChatRoom(userId: List<String>):DocumentReference{
        if(userId.get(0).equals(currentUserId())){
            return allUserCollectionReference().document(userId.get(1))
        }else{
            return allUserCollectionReference().document(userId.get(0))
        }
    }
}