package com.android.myapplication.openapi_codingwithmitch.ui.auth

import androidx.lifecycle.ViewModel
import com.android.myapplication.openapi_codingwithmitch.repository.auth.AuthRepository

class AuthViewModel
constructor(
    val authRepository: AuthRepository
):ViewModel(){

}