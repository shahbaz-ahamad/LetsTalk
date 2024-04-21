package com.shahbaz.letstalk.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shahbaz.letstalk.R
import com.shahbaz.letstalk.databinding.FragmentOTPBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OTPFragment : Fragment() {
    private lateinit var binding: FragmentOTPBinding
    private var OTP: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEditTextListeners()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentOTPBinding.inflate(inflater,container,false)
        return binding.root
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