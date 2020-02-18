package com.android.myapplication.openapi_codingwithmitch.session

import android.app.Application
import com.android.myapplication.openapi_codingwithmitch.persistence.AuthTokenDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(val authTokenDao: AuthTokenDao,
                                 val application: Application){

}