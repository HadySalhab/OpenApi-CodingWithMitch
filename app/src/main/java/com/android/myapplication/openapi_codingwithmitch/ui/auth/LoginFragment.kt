package com.android.myapplication.openapi_codingwithmitch.ui.auth


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.android.myapplication.openapi_codingwithmitch.R
import com.android.myapplication.openapi_codingwithmitch.util.ApiEmptyResponse
import com.android.myapplication.openapi_codingwithmitch.util.ApiErrorResponse
import com.android.myapplication.openapi_codingwithmitch.util.ApiSuccessResponse
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "LoginFragment ${viewModel}")

        //we are just testint the api service
        viewModel.testLogin().observe(viewLifecycleOwner){ response->
            when (response){
                is ApiSuccessResponse->{
                    Timber.d("LOGIN RESPONSE: ${response.body}")
                }
                is ApiErrorResponse->{
                    Timber.d("LOGIN RESPONSE: ${response.errorMessage}")
                }
                is ApiEmptyResponse->{
                    Timber.d("LOGIN RESPONSE: Empty Response")

                }
            }
        }
    }


}
