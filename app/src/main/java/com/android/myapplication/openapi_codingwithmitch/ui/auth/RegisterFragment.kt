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
class RegisterFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "RegisterFragment ${viewModel}")

        //we are just testint the api service
        viewModel.testRegister().observe(viewLifecycleOwner){ response->
            when (response){
                is ApiSuccessResponse ->{
                    Timber.d("REGISTRATION RESPONSE: ${response.body}")
                }
                is ApiErrorResponse ->{
                    Timber.d("REGISTRATION RESPONSE: ${response.errorMessage}")
                }
                is ApiEmptyResponse ->{
                    Timber.d("REGISTRATION RESPONSE: Empty Response")

                }
            }
        }
    }

}
