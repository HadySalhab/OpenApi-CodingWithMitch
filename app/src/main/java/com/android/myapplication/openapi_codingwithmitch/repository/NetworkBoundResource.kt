package com.android.myapplication.openapi_codingwithmitch.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.android.myapplication.openapi_codingwithmitch.ui.common.DataState
import com.android.myapplication.openapi_codingwithmitch.ui.common.Response
import com.android.myapplication.openapi_codingwithmitch.ui.common.ResponseType
import com.android.myapplication.openapi_codingwithmitch.util.*
import com.android.myapplication.openapi_codingwithmitch.util.Constants.Companion.NETWORK_TIMEOUT
import com.android.myapplication.openapi_codingwithmitch.util.Constants.Companion.TESTING_NETWORK_DELAY
import com.android.myapplication.openapi_codingwithmitch.util.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.android.myapplication.openapi_codingwithmitch.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.android.myapplication.openapi_codingwithmitch.util.ErrorHandling.Companion.UNABLE_TODO_OPERATION_WO_INTERNET
import com.android.myapplication.openapi_codingwithmitch.util.ErrorHandling.Companion.UNABLE_TO_RESOLVE_HOST
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main


abstract class NetworkBoundResource<ResponseObject,ViewStateType>
    (
    isNetworkAvailable:Boolean //is there a network connection
) {
    companion object {
        private const val TAG = "AppDebug"
    }
    protected  val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var  job:CompletableJob //for cancellation and completion
    //if the user presses back btn immediately after certain operation (login,post...)
    protected  lateinit var  coroutineScope: CoroutineScope

    init {
        setJob(initNewJob())
        setValue(DataState.loading(isLoading = true,cachedData = null))

        if(isNetworkAvailable){
            coroutineScope.launch {
                //simulate network delay for testing
                delay(TESTING_NETWORK_DELAY)

                withContext(Main){
                    //make network call
                    val apiResponse = createCall()
                    result.addSource(apiResponse){response->
                        result.removeSource(apiResponse)
                        coroutineScope.launch {
                            handleNetworkCall(response)
                        }

                    }
                }
            }
            //THIS WILL LAUNCH AT THE SAME TIME OF THE ABOVE COROUTINE
            GlobalScope.launch(IO){
                delay(NETWORK_TIMEOUT)
                if(!job.isCompleted){
                    Log.e(TAG, "NetworkBoundResource:Job Network Timeout...")
                    job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST)) //cancellation exception will run invokeOnCompletion....
                }

            }

        }else{
            onErrorReturn(UNABLE_TODO_OPERATION_WO_INTERNET,true,false)
        }
    }

    suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>?) {
        when (response) {
            is ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }
            is ApiEmptyResponse -> {
                Log.e(TAG, "NetworkBoundResource: Request returned NOTHING (HTTP 204)")
                onErrorReturn("HTTP 204. Returned Nothing.",true,false)

            }
            is ApiErrorResponse -> {
                Log.e(TAG, "NetworkBoundResource: ${response.errorMessage}")
                onErrorReturn(response.errorMessage,true,false)
            }


        }
    }


    fun onErrorReturn(errorMessage:String?,shouldUseDialog:Boolean,shouldUseToast:Boolean){
        var msg = errorMessage
        var useDialog = shouldUseDialog
        var responseType:ResponseType = ResponseType.None()
        if(msg==null){
            msg = ERROR_UNKNOWN
        }else if (ErrorHandling.isNetworkError(msg)){
            msg = ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }
        if(shouldUseToast) {
            responseType = ResponseType.Toast()
        }
        if(useDialog){
            responseType = ResponseType.Dialog()
        }
        onCompleteJob(DataState.error(
            response = Response(message = msg,responseType = responseType)
        ))
    }

    fun onCompleteJob(dataState: DataState<ViewStateType>){
        GlobalScope.launch (Main){
            job.complete()
            setValue(dataState)
        }
    }
    private fun setValue(dataState:DataState<ViewStateType>){
        result.value = dataState
    }




    @UseExperimental(InternalCoroutinesApi::class)
    private fun initNewJob(): Job {
        Log.d(TAG, "initNewJob: called...")
        job = Job()

        job.invokeOnCompletion (onCancelling = true,invokeImmediately = true,handler = object:CompletionHandler{
            //what will happens when the job goes to completion
            override fun invoke(cause: Throwable?) {
                if(job.isCancelled){
                    Log.e(TAG,"NetworkBoundResource: Job has been cancelled...")
                    cause?.let {
                        onErrorReturn(it.message,false,true)
                    }?:onErrorReturn(ERROR_UNKNOWN,false,true)
                }else if (job.isCompleted){
                    Log.e(TAG, "NetworkBoundResource: Job has been completed...")
                }
            }
        })
        coroutineScope = CoroutineScope(IO+job)
        /*
        * coroutineScope = CoroutineScope(IO) -> to cancel this coroutine we have to write coroutine.cancel()
        * but this will cancel all coroutine within this scope
        *
        * coroutineScope = CoroutineScope(IO+job) -> job.cancel() will cancel only the coroutine within this scope
        * that have this job
        *
        * each transaction will have its own job
        * */
        job.cancel()
        return job
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>
    abstract suspend fun handleApiSuccessResponse(response:ApiSuccessResponse<ResponseObject>)
    abstract fun createCall():LiveData<GenericApiResponse<ResponseObject>>
    abstract fun setJob(job:Job) //keep track of the job
}