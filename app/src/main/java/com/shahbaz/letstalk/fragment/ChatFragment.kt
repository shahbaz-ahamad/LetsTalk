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
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.shahbaz.letstalk.R
import com.shahbaz.letstalk.adapter.RecentChatAdapter
import com.shahbaz.letstalk.adapter.RegisterContactListAdapter
import com.shahbaz.letstalk.databinding.FragmentChatBinding
import com.shahbaz.letstalk.databinding.FragmentStoriesBinding
import com.shahbaz.letstalk.datamodel.ChatRoomModel
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.helper.FirebasseUtils
import com.shahbaz.letstalk.helper.showBottomNavigation
import com.shahbaz.letstalk.sealedclass.Resources
import com.shahbaz.letstalk.viewmodel.AuthViewmodel
import com.shahbaz.letstalk.viewmodel.ChatFragmetViewmodel
import com.shahbaz.letstalk.viewmodel.ChatViewmodel
import com.shahbaz.letstalk.viewmodel.ContactViewmodel
import com.shahbaz.letstalk.viewmodel.UserProfileViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject


@AndroidEntryPoint
class ChatFragment : Fragment(),RecentChatAdapter.OnItemClickListener {
    private lateinit var binding: FragmentChatBinding
    private val READ_CONTACTS_PERMISSION_REQUEST = 101
    private val viewmodel by viewModels<AuthViewmodel>()
    private val userProfileViewmodel by viewModels<UserProfileViewmodel>()
    @Inject
    lateinit var firebaseUtils: FirebasseUtils
    private val recentChatViewmodel by viewModels<ChatFragmetViewmodel>()
    private lateinit var recentChatAdapter: RecentChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recentChatViewmodel.recentChat()
        displayUserInfo()
        binding.addChat.setOnClickListener {
            requestContactsPermission()
        }



        lifecycleScope.launchWhenStarted {
            recentChatViewmodel.recentChatState.collectLatest {
                when (it) {
                    is Resources.Error -> {
                        binding.progressabr.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }

                    is Resources.Loading -> {
                        binding.progressabr.visibility = View.VISIBLE
                    }
                    is Resources.Success -> {
                        binding.progressabr.visibility = View.GONE
                        val options = it.data
                        if (options != null) {
                            setupRecentChatRecyclerView(options)
                            recentChatAdapter.startListening()
                        }
                        recentChatAdapter.notifyDataSetChanged()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setupRecentChatRecyclerView(options: FirestoreRecyclerOptions<ChatRoomModel>) {
        recentChatAdapter = RecentChatAdapter(options, firebaseUtils, requireContext(),this)
        binding.recyclerviewRecentChat.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = recentChatAdapter
            recentChatAdapter.startListening()

        }
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
    ) {
        when (requestCode) {
            READ_CONTACTS_PERMISSION_REQUEST -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted
                    findNavController().navigate(R.id.action_chatFragment_to_contactFragment)
                } else {
                    // Permission denied
                    // Handle this case, e.g., show a message to the user

                    Toast.makeText(requireContext(), "Please Allow Permission", Toast.LENGTH_SHORT)
                        .show()
                }
                return
            }
            // Add more cases if needed for other permission requests
        }
    }


    private fun displayUserInfo() {

        val currentUser = viewmodel.currentUser
        binding.apply {

            if (currentUser?.photoUrl != null) {
                Glide
                    .with(requireContext())
                    .load(currentUser?.photoUrl)
                    .placeholder(R.drawable.profile)
                    .into(profileImage)

                name.text = currentUser?.displayName
            } else {
                profileImage.setImageResource(R.drawable.profile)
                name.text = currentUser?.displayName
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigation()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onItemClick(registerUser: UserProfile) {
        val action =ChatFragmentDirections.actionChatFragmentToChatRoomFragment(userProfile = registerUser)
        findNavController().navigate(action)
    }

    override fun onStop() {
        super.onStop()
        recentChatAdapter.stopListening()
    }

}