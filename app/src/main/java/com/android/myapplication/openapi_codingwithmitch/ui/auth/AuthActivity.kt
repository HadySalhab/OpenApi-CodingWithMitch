package com.android.myapplication.openapi_codingwithmitch.ui.auth

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.android.myapplication.openapi_codingwithmitch.R
import com.android.myapplication.openapi_codingwithmitch.ui.auth.viewmodel.AuthViewModel
import com.android.myapplication.openapi_codingwithmitch.ui.common.BaseActivity
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

    }
}
