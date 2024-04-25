package com.shahbaz.letstalk.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.shahbaz.letstalk.R
import com.shahbaz.letstalk.databinding.FragmentChatRoomBinding
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.helper.hideBottomNavigation
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChatRoomFragment : Fragment() {

    private lateinit var binding: FragmentChatRoomBinding
    private val args by navArgs<ChatRoomFragmentArgs>()
    private lateinit var user:UserProfile
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