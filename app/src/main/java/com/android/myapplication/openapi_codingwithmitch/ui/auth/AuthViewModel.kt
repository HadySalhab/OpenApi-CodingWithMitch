package com.android.myapplication.openapi_codingwithmitch.ui.auth

import androidx.lifecycle.ViewModel
import com.android.myapplication.openapi_codingwithmitch.repository.auth.AuthRepository
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
):ViewModel(){

}