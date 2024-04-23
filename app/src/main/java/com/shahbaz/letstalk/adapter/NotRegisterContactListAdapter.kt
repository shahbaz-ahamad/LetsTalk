package com.shahbaz.letstalk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shahbaz.letstalk.R
import com.shahbaz.letstalk.databinding.ContactListLayoutBinding
import com.shahbaz.letstalk.datamodel.UnregisteredUser

class NotRegisterContactListAdapter(val context: Context) :
    RecyclerView.Adapter<NotRegisterContactListAdapter.MyViewholder>() {
    inner class MyViewholder(val binding: ContactListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currentContact: UnregisteredUser) {
            binding.profileImage.setImageResource(R.drawable.profile)
            binding.name.text=currentContact.name
            binding.taptoChart.text=""
        }

    }


    private val diffUtil = object : DiffUtil.ItemCallback<UnregisteredUser>(){
        override fun areItemsTheSame(
            oldItem: UnregisteredUser,
            newItem: UnregisteredUser
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: UnregisteredUser,
            newItem: UnregisteredUser
        ): Boolean {
            return oldItem == newItem
        }

    }
    val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {
        return MyViewholder(
            ContactListLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        val currentContact = asyncListDiffer.currentList[position]
        holder.bind(currentContact)
    }
}