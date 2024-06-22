package com.shahbaz.letstalk.fragment

import MessageAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shahbaz.letstalk.R
import com.shahbaz.letstalk.databinding.FragmentChatRoomBinding
import com.shahbaz.letstalk.datamodel.MessageModel
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.helper.FirebasseUtils
import com.shahbaz.letstalk.helper.hideBottomNavigation
import com.shahbaz.letstalk.sealedclass.Resources
import com.shahbaz.letstalk.viewmodel.ChatViewmodel
import com.shahbaz.letstalk.viewmodel.UserProfileViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject


@AndroidEntryPoint
class ChatRoomFragment : Fragment() {
    private lateinit var binding: FragmentChatRoomBinding
    private val args by navArgs<ChatRoomFragmentArgs>()
    private lateinit var user: UserProfile
    private val viewModel by viewModels<ChatViewmodel>()
    private val profileViewmodel by viewModels<UserProfileViewmodel>()

    companion object{
        var chatRoomId:String =""
    }

    @Inject
    lateinit var firebasseUtils: FirebasseUtils
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigation()

        chatRoomId= firebasseUtils.currentUserId()
            ?.let { firebasseUtils.getChatRoomId(it, user.userId) }.toString()
        showUserDetails()

        viewModel.getOrCreateChatRoomModel(chatRoomId,user)

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.sendButton.setOnClickListener {
            binding.apply {
                if (messageBox.text.isNotEmpty()) {
                    val message = messageBox.text.toString()
                    viewModel.sendMessage(chatRoomId,message)
                    messageBox.text.clear()
                } else {
                    Toast.makeText(requireContext(), "Can't Send Empty Message", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        setupRecyclerView()

        //set the status to typing
        binding.messageBox.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if(s.toString().trim().length == 0){
                    profileViewmodel.ChangeUserStatus("Online")
                }else{
                    profileViewmodel.ChangeUserStatus("Typing...")
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                profileViewmodel.ChangeUserStatus("Typing...")
            }
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().trim().length == 0){
                    profileViewmodel.ChangeUserStatus("Online")
                }
            }

        })
        lifecycleScope.launchWhenStarted {
            profileViewmodel.userOnlineStatus.collectLatest {
                when(it){
                    is Resources.Error -> {

                    }
                    is Resources.Loading -> {

                    }
                    is Resources.Success -> {
                        if(it.data == "Online"){
                            binding.status.text="Online"
                        }
                        else if(it.data == "Typing..."){
                            binding.status.text="Typing..."
                        }
                        else{
                            binding.status.text=it.data
                        }
                    }
                    else -> Unit
                }
            }
        }
    }


    private fun showUserDetails() {
        binding.apply {
            if (user.userProfileImage != "") {
                Glide
                    .with(requireContext())
                    .load(user.userProfileImage)
                    .placeholder(R.drawable.profile)
                    .into(profileImage)
                name.text = user.userName
            } else {
                name.text = user.userName
            }
        }
    }
    private fun setupRecyclerView() {
        binding.recyclerViewMsg.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            true
        )
        val mAdapter = MessageAdapter(viewModel.messageOptions,viewModel.currentUser?.uid.toString())
        binding.recyclerViewMsg.adapter=mAdapter
        mAdapter.startListening()
        mAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.recyclerViewMsg.smoothScrollToPosition(0)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = args.userProfile
        profileViewmodel.FetchUserOnlineStatus(user.userId)
    }
}


