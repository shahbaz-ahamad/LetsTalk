package com.shahbaz.letstalk.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.PhoneAuthOptions
import com.shahbaz.letstalk.repositiory.AuthRepo
import com.shahbaz.letstalk.sealedclass.PhoneAuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class AuthViewmodel @Inject constructor(
    private val authRepo :AuthRepo
):ViewModel() {

    val verificationState :Flow<PhoneAuthState> =authRepo.verificationState

    val currentUser
        get() = authRepo.currentuser


    val basePhoneAuthOptionsBuilder: PhoneAuthOptions.Builder
        get() = authRepo.basePhoneAuthOptionsBuilder

    fun sendVerificationCode(options: PhoneAuthOptions) {
        authRepo.sendVerificationCode(options)
    }

    fun verifyCode(code: String) {
        authRepo.verifyCode(code)
    }

    fun logout() {
        authRepo.logout()
    }
}