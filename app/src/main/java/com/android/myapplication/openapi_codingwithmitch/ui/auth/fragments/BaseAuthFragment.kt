package com.android.myapplication.openapi_codingwithmitch.ui.auth.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.android.myapplication.openapi_codingwithmitch.ui.auth.viewmodel.AuthViewModel
import com.android.myapplication.openapi_codingwithmitch.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

//the reason of this class, is to be able to share the same viewmodel to all child fragments
abstract class BaseAuthFragment : DaggerFragment() {
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    val TAG: String = "AppDebug"

    lateinit var viewModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.run {
            //The context object is available as a receiver (this)
            //this here does not refer to BaseAuthFragment but instead to the Activity
            //we did this to insure the same context or store is provided to the ViewModelProvider
            //we should not use let here, because this will refer to BaseAuthFragment.
            //since activity is the storeowner, this viewModel will live as long as the activity is not FINISHED.
            ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }
}