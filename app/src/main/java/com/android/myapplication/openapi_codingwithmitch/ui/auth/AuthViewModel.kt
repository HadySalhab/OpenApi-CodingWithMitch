package com.android.myapplication.openapi_codingwithmitch.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.android.myapplication.openapi_codingwithmitch.api.auth.network_responses.LoginResponse
import com.android.myapplication.openapi_codingwithmitch.api.auth.network_responses.RegistrationResponse
import com.android.myapplication.openapi_codingwithmitch.repository.auth.AuthRepository
import com.android.myapplication.openapi_codingwithmitch.util.GenericApiResponse
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
):ViewModel(){

    fun testLogin():LiveData<GenericApiResponse<LoginResponse>>{
        return authRepository.testLoginRequest(
            "mitchelltabian@gmail.com",
            "codingwithmitch1"
        )
    }
    fun testRegister():LiveData<GenericApiResponse<RegistrationResponse>>{
        return authRepository.testRegitrationRequest(
            "mitchelltabian1234@gmail.com",
            "mitchelltabian1234",
            "codingwithmitch1",
            "codingwithmitch1"
        )
    }
}