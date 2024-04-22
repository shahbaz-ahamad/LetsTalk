package com.shahbaz.letstalk.fragment

import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.shahbaz.letstalk.MainActivity
import com.shahbaz.letstalk.R
import com.shahbaz.letstalk.databinding.FragmentOTPBinding
import com.shahbaz.letstalk.sealedclass.PhoneAuthState
import com.shahbaz.letstalk.viewmodel.AuthViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class OTPFragment : Fragment() {
    private lateinit var binding: FragmentOTPBinding
    private val viewmodel by viewModels<AuthViewmodel>()
    private var OTP: String? = null
    private var phoneNumber:String=""



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEditTextListeners()
        binding.tvNumber.text="Enter the OTP Sent to "+phoneNumber
        binding.buttonLogin.setOnClickListener {

            if(OTP != null && OTP?.length == 6){
                Log.d("12",OTP.toString())
                Log.d("1","w")
                viewmodel.verifyCode(OTP!!)
            }else{
                Toast.makeText(requireContext(),"Please Enter OTP",Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentOTPBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let{
            phoneNumber=it.getString("phoneNumber","")
        }

        lifecycleScope.launchWhenStarted {
            viewmodel.verificationState.collectLatest {

                when(it){
                    is PhoneAuthState.VerificationInProgress ->{
                        binding.progressBar.visibility=View.VISIBLE
                    }

                    is PhoneAuthState.SignedInSuccess ->{
                        binding.progressBar.visibility=View.INVISIBLE
                        findNavController().navigate(R.id.action_OTPFragment_to_profileFragment)
                    }
                    is PhoneAuthState.InvalidCode ->{
                        binding.progressBar.visibility=View.INVISIBLE
                        Toast.makeText(requireContext(),"Please Enter Valid Code",Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }
    private fun setupEditTextListeners() {
        binding.apply {
            etOtp1.addTextChangedListener(createTextWatcher(etOtp1, etOtp2))
            etOtp2.addTextChangedListener(createTextWatcher(etOtp2, etOtp3))
            etOtp3.addTextChangedListener(createTextWatcher(etOtp3, etOtp4))
            etOtp4.addTextChangedListener(createTextWatcher(etOtp4, etOtp5))
            etOtp5.addTextChangedListener(createTextWatcher(etOtp5, etOtp6))
            etOtp6.addTextChangedListener(createTextWatcher(etOtp6, null))
        }
    }


    private fun createTextWatcher(currentEditText: View, nextEditText: View?): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1 && nextEditText != null) {
                    nextEditText.requestFocus()
                }


                binding.apply {
                    if (etOtp1.text.isNotEmpty() && etOtp2.text.isNotEmpty() &&
                        etOtp3.text.isNotEmpty() && etOtp4.text.isNotEmpty() &&
                        etOtp5.text.isNotEmpty() && etOtp6.text.isNotEmpty()
                    ) {
                        OTP =
                            "${etOtp1.text}${etOtp2.text}${etOtp3.text}${etOtp4.text}${etOtp5.text}${etOtp6.text}"
                        Log.d("otp", OTP.toString())

                    }
                }
            }
        }
    }

}