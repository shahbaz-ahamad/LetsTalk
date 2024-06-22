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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.oAuthProvider
import com.shahbaz.letstalk.R
import com.shahbaz.letstalk.adapter.NotRegisterContactListAdapter
import com.shahbaz.letstalk.adapter.RegisterContactListAdapter
import com.shahbaz.letstalk.databinding.FragmentContactBinding
import com.shahbaz.letstalk.datamodel.UnregisteredUser
import com.shahbaz.letstalk.datamodel.UserProfile
import com.shahbaz.letstalk.helper.CheckInternet
import com.shahbaz.letstalk.helper.hideBottomNavigation
import com.shahbaz.letstalk.sealedclass.Resources
import com.shahbaz.letstalk.viewmodel.ContactViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ContactFragment : Fragment(),RegisterContactListAdapter.OnItemClickListener {
    private lateinit var binding: FragmentContactBinding
    private val viewmodel by viewModels<ContactViewmodel>()


    private val registerContactListAdapter :RegisterContactListAdapter by lazy {
        RegisterContactListAdapter(requireContext(),this,viewmodel.currentUser?.uid.toString())
    }


    private val notRegisterContactListAdapter :NotRegisterContactListAdapter by lazy {
        NotRegisterContactListAdapter(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Inflate the layout for this fragment
        binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigation()
        val contact = viewmodel.FetchContact()

        if(CheckInternet.isInternetAvailable(requireContext())){
            viewmodel.FetchRegisterUser(contact)
        }else{
           Toast.makeText(requireContext(),"Connect to Internet",Toast.LENGTH_SHORT).show()
        }

        SetupRecyclerView()
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewmodel.registerContactsState.collectLatest {
                when(it){
                    is Resources.Loading ->{
                        binding.progressBar.visibility=View.VISIBLE
                    }
                    is Resources.Success ->{
                        binding.progressBar.visibility=View.INVISIBLE
                        val registerContact = it.data
                        registerContactListAdapter.asyncListDiffer.submitList(registerContact)
                    }
                    is Resources.Error ->{
                        binding.progressBar.visibility=View.INVISIBLE
                        Toast.makeText(requireContext(),"Failed to Fetch Contact",Toast.LENGTH_SHORT).show()
                    }else ->Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewmodel.unRegisterContactState.collectLatest {
                when(it){
                    is Resources.Loading ->{
                    }
                    is Resources.Success ->{
                        val unRegisterContact = it.data
                        notRegisterContactListAdapter.asyncListDiffer.submitList(unRegisterContact)
                    }
                    is Resources.Error ->{
                        Toast.makeText(requireContext(),"Failed to Fetch UnRegister Contact",Toast.LENGTH_SHORT).show()
                    }else ->Unit
                }

            }
        }


    }


    fun SetupRecyclerView(){
        binding.recyclerviewContactOnLetsTalk.apply {
            layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            adapter=registerContactListAdapter
        }
        binding.recyclerviewContactNotOnLetsTalk.apply {
            layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            adapter=notRegisterContactListAdapter
        }
    }

    //to perfrom onCLick on the contact to go chatroom fragement
    override fun onItemClick(registerUser: UserProfile){
        val action =ContactFragmentDirections.actionContactFragmentToChatRoomFragment(registerUser)
        findNavController().navigate(action)
    }
}