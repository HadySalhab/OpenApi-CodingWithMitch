package com.android.myapplication.openapi_codingwithmitch.repository.auth

import com.android.myapplication.openapi_codingwithmitch.api.auth.OpenApiAuthService
import com.android.myapplication.openapi_codingwithmitch.persistence.AccountPropertiesDao
import com.android.myapplication.openapi_codingwithmitch.persistence.AuthTokenDao
import com.android.myapplication.openapi_codingwithmitch.session.SessionManager

class AuthRepository  constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
)