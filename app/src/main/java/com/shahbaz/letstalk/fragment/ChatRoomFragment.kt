package com.shahbaz.letstalk.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.shahbaz.letstalk.R
import com.shahbaz.letstalk.adapter.MessageAdapter
import com.shahbaz.letstalk.databinding.FragmentChatRoomBinding
import com.shahbaz.letstalk.datamodel.MessageModel
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.helper.hideBottomNavigation
import com.shahbaz.letstalk.sealedclass.Resources
import com.shahbaz.letstalk.viewmodel.ChatViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class ChatRoomFragment : Fragment(){

    private lateinit var binding: FragmentChatRoomBinding
    private val args by navArgs<ChatRoomFragmentArgs>()
    private lateinit var user:UserProfile
    private val viewModel by viewModels<ChatViewmodel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentChatRoomBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigation()
        user=args.userProfile
        showUserDetails()
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.sendButton.setOnClickListener {
            binding.apply {
                if(messageBox.text.isNotEmpty()){
                    val message =messageBox.text.toString()
                    viewModel.sendMessage(user,message)
                    messageBox.text.clear()
                }else{
                    Toast.makeText(requireContext(),"Can't Send Empty Message",Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.fetchedMesage(user)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.chatFetched.collectLatest {
                when(it){
                    is Resources.Loading ->{

                    }
                    is Resources.Success ->{
                        val message = it.data
                        if(message != null){
                            binding.apply {
                                val currentUser= viewModel.currentUser
                                recyclerViewMsg.layoutManager=LinearLayoutManager(requireContext())
                                val adapterMsg = MessageAdapter(message,currentUser?.uid.toString())
                                recyclerViewMsg.adapter=adapterMsg
                                Log.d("message",message.toString())
                            }
                        }
                    }
                    is Resources.Error ->{

                    }else -> Unit
                }
            }
        }
    }
    private fun showUserDetails(){
        binding.apply {
            if (user.userProfileImage != ""){
                Glide
                    .with(requireContext())
                    .load(user.userProfileImage)
                    .placeholder(R.drawable.profile)
                    .into(profileImage)
                name.text = user.userName
            }else{
                name.text = user.userName
            }
        }
    }


}