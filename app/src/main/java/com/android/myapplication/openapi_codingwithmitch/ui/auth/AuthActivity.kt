package com.android.myapplication.openapi_codingwithmitch.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.android.myapplication.openapi_codingwithmitch.R
import com.android.myapplication.openapi_codingwithmitch.ui.auth.viewmodel.AuthViewModel
import com.android.myapplication.openapi_codingwithmitch.ui.common.BaseActivity
import com.android.myapplication.openapi_codingwithmitch.ui.common.ResponseType
import com.android.myapplication.openapi_codingwithmitch.ui.main.MainActivity
import com.android.myapplication.openapi_codingwithmitch.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class AuthActivity : BaseActivity(), NavController.OnDestinationChangedListener {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        //AuthViewModel has activity as the storeOwner
        viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        findNavController(R.id.auth_host_fragment).addOnDestinationChangedListener(this) //we registered this activity as a listener to any navigation, so we can cancel the job
        subscribeObservers()
    }
    fun subscribeObservers(){
        viewModel.dataState.observe(this, Observer { dataState->

            dataState.data?.let { data->
                data.data?.let { event->
                    //we want to handle the data once
                    //rotation will not trigger the event again
                    //this method will return the actual generic parameter, which is AuthViewState in this case
                    event.getContentIfNotHandled()?.let {
                        it.authToken?.let {
                            Log.d(TAG, "AuthActivity, DataState: ${it}")
                            //we are updating a field in the AuthViewState
                            //which will update the AuthViewState itself
                            // and trigger any observer
                            viewModel.setAuthToken(it)
                        }
                    }
                }
                data.response?.let { event ->
                    event.getContentIfNotHandled()?.let {
                        when(it.responseType){
                            is ResponseType.Dialog->{
                                //show dialog
                            }
                            is ResponseType.Toast->{
                                // show toast
                            }
                            is ResponseType.None->{
                                Log.e(TAG, "AuthActivity: Response: ${it.message} ")
                            }
                        }
                    }
                }
            }
        })


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

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        viewModel.cancelActiveJobs()
    }
}
