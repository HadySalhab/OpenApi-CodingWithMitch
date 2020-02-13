package com.android.myapplication.openapi_codingwithmitch.session

import android.app.Application
import com.android.myapplication.openapi_codingwithmitch.persistence.AuthTokenDao

class SessionManager constructor(val authTokenDao: AuthTokenDao,
                                 val application: Application){

}