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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.PhoneAuthOptions
import com.shahbaz.letstalk.R
import com.shahbaz.letstalk.databinding.FragmentLoginBinding
import com.shahbaz.letstalk.sealedclass.PhoneAuthState
import com.shahbaz.letstalk.viewmodel.AuthViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding:FragmentLoginBinding
    private val viewmodel by viewModels<AuthViewmodel>()
    private var phoneNumber:String=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSendOTP.setOnClickListener {
            val countryCode=binding.countryCode.selectedCountryCode
            val number=binding.etPhoneNumber.text
            phoneNumber="+"+countryCode+number
            Log.d("phoneNumber",phoneNumber)

            viewmodel.sendVerificationCode(
                viewmodel.basePhoneAuthOptionsBuilder
                    .setActivity(requireActivity())
                    .setTimeout(60L,TimeUnit.SECONDS)
                    .setPhoneNumber(phoneNumber)
                    .build()
            )

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewmodel.verificationState.collectLatest {
                when(it){

                    is PhoneAuthState.InvalidNumber ->{
                        Toast.makeText(requireContext(),"Invalid Number",Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility=View.INVISIBLE
                    }

                    is PhoneAuthState.SendingCode ->{
                        binding.progressBar.visibility=View.VISIBLE
                    }

                    is PhoneAuthState.VerificationCodeSent ->{
                        val action =LoginFragmentDirections.actionLoginFragmentToOTPFragment(phoneNumber)
                        findNavController().navigate(action)
                        binding.progressBar.visibility=View.INVISIBLE
                    }
                    else -> Unit
                }
            }
        }
    }
}