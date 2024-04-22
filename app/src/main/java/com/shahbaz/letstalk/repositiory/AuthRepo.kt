package com.shahbaz.letstalk.repositiory

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.shahbaz.letstalk.LoginActivity
import com.shahbaz.letstalk.sealedclass.PhoneAuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AuthRepo @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {


    private val _verificationState = MutableStateFlow<PhoneAuthState>(PhoneAuthState.Idle)
    val verificationState = _verificationState.asStateFlow()


    val currentuser
        get() =
            firebaseAuth.currentUser

    private val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            //for the wrong number
            _verificationState.value = PhoneAuthState.InvalidNumber(p0)
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(verificationId, token)
            Log.d("TAG", "onCodeSent:$verificationId")
            //this@AuthRepo.verificationId = verificationId
            LoginActivity.VERFICATIONID=verificationId
            Log.d("TAG", "onCodeSent:${LoginActivity.VERFICATIONID}")
            _verificationState.value=PhoneAuthState.VerificationCodeSent

        }
    }

    val basePhoneAuthOptionsBuilder: PhoneAuthOptions.Builder
        get() = PhoneAuthOptions.Builder(firebaseAuth)
            .setCallbacks(callback)

    fun sendVerificationCode(options: PhoneAuthOptions) {
        _verificationState.value = PhoneAuthState.SendingCode
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyCode(code: String) {


//        verificationId?.let {
//            signInWithPhoneAuthCredential(
//                PhoneAuthProvider.getCredential(it, code)
//            )
//        }

        if (LoginActivity.VERFICATIONID!= null) {
            signInWithPhoneAuthCredential(
                PhoneAuthProvider.getCredential(LoginActivity.VERFICATIONID!!, code)
            )
        } else {
            // Handle the case where verificationId is null
            Log.e("AuthRepo", "null verification id")
        }
    }


    fun logout() = firebaseAuth.signOut()

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        _verificationState.value = PhoneAuthState.VerificationInProgress
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Success","signInWithCredential:success")
                    _verificationState.value = PhoneAuthState.SignedInSuccess(
                        task.result.user!!.uid
                    )
                    LoginActivity.VERFICATIONID = null
                } else {
                    Log.d("failure", task.exception.toString())
                    _verificationState.value = PhoneAuthState.InvalidCode(
                        task.exception ?: FirebaseException("Verification Failed")
                    )
                }
            }
    }
}