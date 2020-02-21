package com.android.myapplication.openapi_codingwithmitch.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.lifecycle.Observer
import com.android.myapplication.openapi_codingwithmitch.R
import com.android.myapplication.openapi_codingwithmitch.ui.auth.AuthActivity
import com.android.myapplication.openapi_codingwithmitch.ui.common.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity :BaseActivity(){
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_main)

        tool_bar.setOnClickListener{
            sessionManager.logout()
        }
        subscribeObservers()
    }

    fun subscribeObservers(){
        //we have access to the sessionManager, because it is part of the BaseActivity
        //we observe it , because if the user logged out we have to move back to the authActivity
        sessionManager.cachedToken.observe(this, Observer { authToken->
            Log.d(TAG, "MainActivity: subscribeObservers: AuthToken: ${authToken}")
            if(authToken==null || authToken.account_pk==-1 || authToken.token == null){
                navAuthActivity()
            }
        })
    }
    private fun navAuthActivity(){
        val intent = Intent(this,AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}