package com.shahbaz.letstalk.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.shahbaz.letstalk.MainActivity
import com.shahbaz.letstalk.R
import com.shahbaz.letstalk.databinding.FragmentProfileBinding
import com.shahbaz.letstalk.sealedclass.UserProfileSetupState
import com.shahbaz.letstalk.viewmodel.UserProfileViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest




@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private var SelectedImageURI: Uri? = null
    private val STORAGE_PERMISSION_CODE = 101
    private val READ_MEDIA_IMAGES_PERMISSION_CODE = 102
    private val PICK_IMAGE=1
    private val viewmodel by viewModels<UserProfileViewmodel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewmodel.profileUpdateState.collectLatest {
                when(it){

                    is UserProfileSetupState.Loading ->{
                        binding.progressBar.visibility=View.VISIBLE
                    }
                    is UserProfileSetupState.Success ->{
                        binding.progressBar.visibility=View.INVISIBLE
                        val intent = Intent(requireContext(),MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    is UserProfileSetupState.Error ->{
                        binding.progressBar.visibility=View.INVISIBLE
                        Log.d("Error",it.toString())
                        Toast.makeText(requireContext(),it.toString(),Toast.LENGTH_SHORT).show()
                    }else ->Unit
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //choose profile picture
        binding.profileImage.setOnClickListener {
            checkPermission()
        }

        binding.buttonSave.setOnClickListener {
            val name = binding.etName.text.toString()
            if (name != null) {
                if(SelectedImageURI == null){
                    viewmodel.addDataToDatabase(name,null)
                }else{
                    viewmodel.addDataToDatabase(name,SelectedImageURI   )
                }
            }
        }
    }


    fun OpenGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent,"Please Select Profile Picture"),PICK_IMAGE)
    }


    fun checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if permission is not granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                        READ_MEDIA_IMAGES_PERMISSION_CODE
                    )
                } else {
                    // Permission already granted
                    // You can perform your operation here, e.g., open gallery
                    OpenGallery()
                }
            } else {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        STORAGE_PERMISSION_CODE
                    )
                } else {
                    // Permission already granted
                    // You can perform your operation here, e.g., open gallery
                    OpenGallery()
                }
            }
        } else {
            // Permission automatically granted on lower versions
            // You can perform your operation here, e.g., open gallery
           OpenGallery()
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        // Check if permission is granted
        if (requestCode == STORAGE_PERMISSION_CODE || requestCode == READ_MEDIA_IMAGES_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, perform your operation here, e.g., open gallery
                OpenGallery()
            } else {
                // Permission denied, show a toast or alert dialog indicating why permission is necessary
                Toast.makeText(
                    requireContext(),
                    "Permission Denied! You cannot access the gallery without permission.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE && Activity.RESULT_OK == resultCode){
            if(data != null){
                SelectedImageURI=data.data

                Glide
                    .with(requireContext())
                    .load(SelectedImageURI.toString())
                    .centerCrop()
                    .placeholder(R.drawable.profile)
                    .into(binding.profileImage)
            }
        }
    }
}