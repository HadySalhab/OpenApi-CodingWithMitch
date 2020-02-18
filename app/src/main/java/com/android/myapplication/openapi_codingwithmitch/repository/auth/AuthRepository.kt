package com.android.myapplication.openapi_codingwithmitch.repository.auth

import androidx.lifecycle.LiveData
import com.android.myapplication.openapi_codingwithmitch.api.auth.OpenApiAuthService
import com.android.myapplication.openapi_codingwithmitch.api.auth.network_responses.LoginResponse
import com.android.myapplication.openapi_codingwithmitch.api.auth.network_responses.RegistrationResponse
import com.android.myapplication.openapi_codingwithmitch.persistence.auth.AccountPropertiesDao
import com.android.myapplication.openapi_codingwithmitch.persistence.auth.AuthTokenDao
import com.android.myapplication.openapi_codingwithmitch.session.SessionManager
import com.android.myapplication.openapi_codingwithmitch.util.GenericApiResponse

class AuthRepository  constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
){

    fun testLoginRequest(email:String,password:String):LiveData<GenericApiResponse<LoginResponse>>{
        return openApiAuthService.login(email,password)
    }
    fun testRegitrationRequest(
        email:String,
        username:String,
        password: String,
        password2: String
    ):LiveData<GenericApiResponse<RegistrationResponse>>{
        return openApiAuthService.register(
            email,
            username,
            password,
            password2
        )
    }
}