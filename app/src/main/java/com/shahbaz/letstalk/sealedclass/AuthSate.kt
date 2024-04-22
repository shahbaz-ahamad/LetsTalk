package com.shahbaz.letstalk.sealedclass

sealed interface PhoneAuthState{

    //it will define intial state when nothing is done
    object Idle : PhoneAuthState
    class InvalidNumber(val exception: Exception) : PhoneAuthState
    object SendingCode : PhoneAuthState
    object VerificationCodeSent : PhoneAuthState
    object VerificationInProgress : PhoneAuthState
    class InvalidCode(val exception: Exception) : PhoneAuthState
    class SignedInSuccess(val uid: String) : PhoneAuthState
}
