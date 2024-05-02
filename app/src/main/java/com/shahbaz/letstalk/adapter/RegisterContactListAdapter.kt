package com.shahbaz.letstalk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shahbaz.letstalk.R
import com.shahbaz.letstalk.databinding.ContactListLayoutBinding
import com.shahbaz.letstalk.datamodel.UserProfile

class RegisterContactListAdapter(
    val context: Context,
    val clickListener : OnItemClickListener,
    val currentUser :String
    ) :
    RecyclerView.Adapter<RegisterContactListAdapter.MyViewholder>() {
    inner class MyViewholder(val binding: ContactListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currentContact: UserProfile?) {
            if (currentContact != null) {
                if (currentContact?.userProfileImage != "") {
                    Glide
                        .with(context)
                        .load(currentContact.userProfileImage)
                        .placeholder(R.drawable.profile)
                        .into(binding.profileImage)

                    if(currentUser == currentContact.userId){
                        binding.name.text = currentContact.userName+"(You)"
                    }else{
                        binding.name.text = currentContact.userName
                    }
                }else{
                    binding.profileImage.setImageResource(R.drawable.profile)
                    if(currentUser == currentContact.userId){
                        binding.name.text = currentContact.userName+"(You)"
                    }else{
                        binding.name.text = currentContact.userName

                    }
                }
            }
            itemView.setOnClickListener{
              clickListener.onItemClick(currentContact!!)
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<UserProfile>() {
        override fun areItemsTheSame(oldItem: UserProfile, newItem: UserProfile): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: UserProfile, newItem: UserProfile): Boolean {
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


    interface OnItemClickListener{
        fun onItemClick(registerUser: UserProfile)
    }

}