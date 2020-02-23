package com.android.myapplication.openapi_codingwithmitch.ui.common

import android.util.Log
import com.android.myapplication.openapi_codingwithmitch.session.SessionManager
import com.android.myapplication.openapi_codingwithmitch.ui.DataStateChangeListener
import com.android.myapplication.openapi_codingwithmitch.ui.displayErrorDialog
import com.android.myapplication.openapi_codingwithmitch.ui.displaySuccesDialog
import com.android.myapplication.openapi_codingwithmitch.ui.displayToast
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity :DaggerAppCompatActivity(),DataStateChangeListener{
    val TAG = "AppDebug"

    @Inject
    lateinit var sessionManager: SessionManager

    //when the dataState changes , we should handle the error,loading , and the response in data
    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            GlobalScope.launch (Main){
                displayProgressBar(it.loading.isLoading) //loading
                it.error?.let { errorEvent-> //error
                    handleStateError(errorEvent)
                }
                it.data?.let {
                    it.response?.let { responseEvent-> //response in data state
                        handleStateResponse(responseEvent)
                    }
                }
            }
        }
    }

    private fun handleStateError(errorEvent: Event<StateError>){
        errorEvent.getContentIfNotHandled()?.let {
            //sealed class
            when(it.response.responseType){
                is ResponseType.Toast->{
                    it.response.message?.let { message->
                        displayToast(message)
                    }
                }
                is ResponseType.Dialog->{
                    it.response.message?.let { message->
                        displayErrorDialog(message)
                    }
                }
                is ResponseType.None->{
                    Log.e(TAG, "handleStateError: ${it.response.message}")
                }
                
            }
        }
    }

    private fun handleStateResponse(event: Event<Response>){
        event.getContentIfNotHandled()?.let {
            //sealed class
            when(it.responseType){
                is ResponseType.Toast->{
                    it.message?.let { message->
                        displayToast(message)
                    }
                }
                is ResponseType.Dialog->{
                    it.message?.let { message->
                        displaySuccesDialog(message)
                    }
                }
                is ResponseType.None->{
                    Log.e(TAG, "handleStateError: ${it.message}")
                }

            }
        }
    }




    abstract fun displayProgressBar(bool:Boolean)
}