package com.android.myapplication.openapi_codingwithmitch.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.myapplication.openapi_codingwithmitch.R
import com.android.myapplication.openapi_codingwithmitch.ui.auth.viewmodel.AuthViewModel
import com.android.myapplication.openapi_codingwithmitch.ui.common.BaseActivity
import com.android.myapplication.openapi_codingwithmitch.ui.main.MainActivity
import com.android.myapplication.openapi_codingwithmitch.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class AuthActivity : BaseActivity() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        //AuthViewModel has activity as the storeOwner
        viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        subscribeObservers()
    }
    fun subscribeObservers(){
        //the viewState will be frequently changed.
        //lets say the user land on the login fragment and submit his detail
        //this will change the viewState that Holds the AuthToken objects
        //here we are passing the authToken to the session manager to try to login
        viewModel.viewState.observe(this, Observer {
            it.authToken?.let {
                sessionManager.login(it)
            }
        })

        //when this activity is launched,
        //we check if authToken object exist with the pk!=-1 and the token !=null
        //that means that the user was previously logged in and didn't logout
        //that way navigate directly to the MainActivity
        //we want to avoid asking the user to login every single time
        //and this will be triggered when the cachedtoken is changed inside the session manager (user trying to login)
        sessionManager.cachedToken.observe(this, Observer { authToken->
            Log.d(TAG, "AuthActivity: subscribeObservers: AuthToken: ${authToken}")
            if(authToken!=null && authToken.account_pk!=-1 || authToken!=null){
                navMainActivity()
            }
        })
    }
    private fun navMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
