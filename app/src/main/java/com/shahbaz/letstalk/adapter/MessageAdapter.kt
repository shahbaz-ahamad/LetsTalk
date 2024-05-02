package com.shahbaz.letstalk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shahbaz.letstalk.R
import com.shahbaz.letstalk.databinding.ReceiveMessageLayoutBinding
import com.shahbaz.letstalk.databinding.SendMessageLayoutBinding
import com.shahbaz.letstalk.datamodel.MessageModel

class MessageAdapter(private val messageModel: ArrayList<MessageModel>, private val uid: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val ITEM_SEND = 1
    val ITEM_RECEIVED = 2


    //to find out wheter it is received msg or send msg
    override fun getItemViewType(position: Int): Int {
        return if (uid == messageModel[position].senderId) ITEM_SEND else ITEM_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SEND)
            SendViewholder(
                LayoutInflater.from(parent.context).inflate(R.layout.send_message_layout,parent,false)
            )
        else
            ReceivedViewholder(
                LayoutInflater.from(parent.context).inflate(R.layout.receive_message_layout,parent,false)
            )


    }

    override fun getItemCount(): Int {
        return messageModel.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message =messageModel[position]

        if(holder.itemViewType == ITEM_SEND){
            val viewholder =holder as SendViewholder
            viewholder.binding.sendMessage.text=message.message
        }else{
            val viewholder = holder as ReceivedViewholder
            viewholder.binding.receivedMessage.text=message.message

        }
    }

    inner class SendViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = SendMessageLayoutBinding.bind(itemView)
    }

    inner class ReceivedViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = ReceiveMessageLayoutBinding.bind(itemView)
    }
}