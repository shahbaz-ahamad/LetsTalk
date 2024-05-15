package com.shahbaz.letstalk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.shahbaz.letstalk.R
import com.shahbaz.letstalk.databinding.RecentChatBinding
import com.shahbaz.letstalk.datamodel.ChatRoomModel
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.helper.FirebasseUtils
import java.text.DateFormat
import java.text.SimpleDateFormat


class RecentChatAdapter(
    options: FirestoreRecyclerOptions<ChatRoomModel>,
    val firebasseUtils: FirebasseUtils,
    val context1: Context,
    val clickListner:OnItemClickListener
) :
    FirestoreRecyclerAdapter<ChatRoomModel, RecentChatAdapter.RecentChatViewmodel>(options) {
    class RecentChatViewmodel(val binding: RecentChatBinding) :
        RecyclerView.ViewHolder(binding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentChatViewmodel {
        return RecentChatViewmodel(
            RecentChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: RecentChatViewmodel,
        position: Int,
        model: ChatRoomModel
    ) {

        val isLastMessageSentByMe = model.lastMessageSenderId.equals(firebasseUtils.currentUserId())
        firebasseUtils.getOtherUserFromChatRoom(model.userId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val usermodel = task.result.toObject(UserProfile::class.java)
                    holder.binding.name.text = usermodel?.userName

                    if (usermodel?.userProfileImage != "") {
                        Glide.with(context1)
                            .load(usermodel?.userProfileImage)
                            .placeholder(R.drawable.profile)
                            .into(holder.binding.profileImage)

                    } else {
                        holder.binding.profileImage.setImageResource(R.drawable.profile)
                    }

                    if(isLastMessageSentByMe){
                        holder.binding.taptoChart.text="You:"+model.lastMessage
                    }else{
                        holder.binding.taptoChart.text="In:"+model.lastMessage

                    }

                    holder.binding.lastMessageTime.text=SimpleDateFormat("HH:MM").format(model.lastMessagetimeStamp.toDate())
                    holder.itemView.setOnClickListener {
                        clickListner.onItemClick(usermodel!!)
                    }
                }


            }


    }


    interface OnItemClickListener{
        fun onItemClick(registerUser: UserProfile)
    }
}