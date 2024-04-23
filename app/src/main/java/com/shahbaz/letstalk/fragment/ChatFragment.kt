package com.shahbaz.letstalk.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.shahbaz.letstalk.R
import com.shahbaz.letstalk.databinding.FragmentChatBinding
import com.shahbaz.letstalk.databinding.FragmentStoriesBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChatFragment : Fragment() {

    private lateinit var binding:FragmentChatBinding
    private val READ_CONTACTS_PERMISSION_REQUEST = 101
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
        binding.addChat.setOnClickListener {
            requestContactsPermission()
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

}