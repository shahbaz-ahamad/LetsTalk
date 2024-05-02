package com.shahbaz.letstalk.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.shahbaz.letstalk.R
import com.shahbaz.letstalk.adapter.RegisterContactListAdapter
import com.shahbaz.letstalk.databinding.FragmentChatBinding
import com.shahbaz.letstalk.databinding.FragmentStoriesBinding
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.helper.showBottomNavigation
import com.shahbaz.letstalk.sealedclass.Resources
import com.shahbaz.letstalk.viewmodel.AuthViewmodel
import com.shahbaz.letstalk.viewmodel.ChatFragmetViewmodel
import com.shahbaz.letstalk.viewmodel.ChatViewmodel
import com.shahbaz.letstalk.viewmodel.ContactViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class ChatFragment : Fragment(),RegisterContactListAdapter.OnItemClickListener {
    private lateinit var binding:FragmentChatBinding
    private val READ_CONTACTS_PERMISSION_REQUEST = 101
    private val viewmodel by viewModels<AuthViewmodel>()
    private val recentViewmodel by viewModels<ChatFragmetViewmodel>()
    private val contactViewmodel by viewModels<ContactViewmodel>()
    private val registerContactListAdapter :RegisterContactListAdapter by lazy {
        RegisterContactListAdapter(requireContext(),this,contactViewmodel.currentUser?.uid.toString())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentChatBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayUserInfo()
        binding.addChat.setOnClickListener {
            requestContactsPermission()
        }

        recentViewmodel.recentUser()
        recyclerview()
    }


    fun requestContactsPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.READ_CONTACTS
                )
            ) {
                // Show an explanation to the user
                // You can show a dialog or toast explaining why the permission is needed
                // Then request the permission
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    READ_CONTACTS_PERMISSION_REQUEST
                )
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    READ_CONTACTS_PERMISSION_REQUEST
                )
            }
        } else {
            // Permission has already been granted
            // You can proceed with retrieving contacts
            findNavController().navigate(R.id.action_chatFragment_to_contactFragment)
        }
    }


    // Override onRequestPermissionsResult to handle the permission request response
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ){
        when (requestCode) {
            READ_CONTACTS_PERMISSION_REQUEST -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted
                   findNavController().navigate(R.id.action_chatFragment_to_contactFragment)
                } else {
                    // Permission denied
                    // Handle this case, e.g., show a message to the user

                    Toast.makeText(requireContext(),"Please Allow Permission",Toast.LENGTH_SHORT).show()
                }
                return
            }
            // Add more cases if needed for other permission requests
        }
    }


    private fun displayUserInfo() {

        val currentUser = viewmodel.currentUser
        binding.apply {

            if(currentUser?.photoUrl != null){
                Glide
                    .with(requireContext())
                    .load(currentUser?.photoUrl)
                    .placeholder(R.drawable.profile)
                    .into(profileImage)

                name.text= currentUser?.displayName
            }else{
                profileImage.setImageResource(R.drawable.profile)
                name.text=currentUser?.displayName
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigation()
    }

    override fun onItemClick(recentUser: UserProfile) {
        val action = ChatFragmentDirections.actionChatFragmentToChatRoomFragment(recentUser)
        findNavController().navigate(action)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       lifecycleScope.launchWhenStarted {
           recentViewmodel.chatFragmentrepoState.collectLatest {
               when(it){
                   is Resources.Error ->{
                       binding.progressabr.visibility=View.GONE
                       Toast.makeText(requireContext(),it.toString(),Toast.LENGTH_SHORT).show()
                   }
                   is Resources.Loading -> {
                       binding.progressabr.visibility=View.VISIBLE
                       Toast.makeText(requireContext(),"Loading",Toast.LENGTH_SHORT).show()
                   }
                   is Resources.Success ->{
                       val data = it.data
                       registerContactListAdapter.asyncListDiffer.submitList(data)
                       Log.d("recent",data.toString())
                       binding.progressabr.visibility=View.GONE
                   }
                   else ->Unit
               }
           }
       }
    }


    fun recyclerview(){
        binding.apply {
            recyclerviewRecentChat.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            recyclerviewRecentChat.adapter=registerContactListAdapter
        }
    }
}