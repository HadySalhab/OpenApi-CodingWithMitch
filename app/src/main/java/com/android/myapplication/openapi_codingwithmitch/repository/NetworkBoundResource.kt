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


abstract class NetworkBoundResource<ResponseObject, ViewStateType>
    (
    isNetworkAvailable: Boolean //is there a network connection
) {
    companion object {
        private const val TAG = "AppDebug"
    }

    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    //for cancellation and completion, subclass of JOB and can be completed using .complete()
    //think of job as subcategory under certain grouping (coroutineScope)
    protected lateinit var job: CompletableJob
    //if the user presses back btn immediately after certain operation (login,post...)
    protected lateinit var coroutineScope: CoroutineScope //coroutineScope is like grouping coroutines (which can be thought as jobs)

    init {
        setJob(initNewJob())
        setValue(DataState.loading(isLoading = true, cachedData = null))

        if (isNetworkAvailable) {
            //this will launch a coroutine attached to the job subcategory and on IO dispatcher
            coroutineScope.launch {
                //simulate network delay for testing
                delay(TESTING_NETWORK_DELAY)

                //switching the context of the coroutine above
                withContext(Main) {
                    //make network call
                    val apiResponse = createCall()
                    result.addSource(apiResponse) { response ->
                        result.removeSource(apiResponse)
                        //this will launch another coroutine attached to the job subcategory and on IO dispatcher
                        coroutineScope.launch {
                            handleNetworkCall(response)
                        }

                    }
                }
            }
            //THIS WILL LAUNCH AT THE SAME TIME OF THE ABOVE COROUTINE
            GlobalScope.launch(IO) {
                delay(NETWORK_TIMEOUT)
                //checking if the coroutines within the job subcategory  has finished their  jobs
                if (!job.isCompleted) {
                    Log.e(TAG, "NetworkBoundResource:Job Network Timeout...")
                    job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST)) //cancellation exception will run invokeOnCompletion....
                }

            }

        } else {
            onErrorReturn(UNABLE_TODO_OPERATION_WO_INTERNET, true, false)
        }
    }

    //this is running on the IO dispatcher
    suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>?) {
        when (response) {
            is ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }
            is ApiEmptyResponse -> {
                Log.e(TAG, "NetworkBoundResource: Request returned NOTHING (HTTP 204)")
                onErrorReturn("HTTP 204. Returned Nothing.", true, false) //this will call onCompleteJob

            }
            is ApiErrorResponse -> {
                Log.e(TAG, "NetworkBoundResource: ${response.errorMessage}") //this will call onCompleteJob
                onErrorReturn(response.errorMessage, true, false)
            }


        }
    }


    //onErrorReturn will complete the job by returning an error
    fun onErrorReturn(errorMessage: String?, shouldUseDialog: Boolean, shouldUseToast: Boolean) {
        var msg = errorMessage
        var useDialog = shouldUseDialog
        var responseType: ResponseType = ResponseType.None()
        if (msg == null) {
            msg = ERROR_UNKNOWN
        } else if (ErrorHandling.isNetworkError(msg)) {
            msg = ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }
        if (shouldUseToast) {
            responseType = ResponseType.Toast()
        }
        if (useDialog) {
            responseType = ResponseType.Dialog()
        }
        //this will terminate job (we can do that because job is of type CompletableJob)
        //this will invoke invokeOnCompletion callback
        //and update the mediatorlivedata.
        onCompleteJob(
            DataState.error(
                response = Response(message = msg, responseType = responseType)
            )
        )
    }

    fun onCompleteJob(dataState: DataState<ViewStateType>) {
        //we need the main dispatcher so we can set value to the livedata
        GlobalScope.launch(Main) {
            //terminate all coroutines within this job subcategory
            job.complete()       //this will invoke invokeOnCompletion callback
            setValue(dataState)  //and update the mediatorlivedata.
        }
    }

    private fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }


    @UseExperimental(InternalCoroutinesApi::class)
    private fun initNewJob(): Job {
        Log.d(TAG, "initNewJob: called...")
        job = Job()


        /*
        * Registers handler that is **synchronously** invoked once on cancellation or completion of this job.
        * When job was already cancelled and is completed its execution, then the handler is immediately invoked
        * with a job's cancellation cause or `null` unless [invokeImmediately] is set to false.
        * Otherwise, handler will be invoked once when this job is cancelled or is complete.
        *
        *
        * */
        /*
        * @param onCancelling when `true`, then the [handler] is invoked as soon as this job transitions to _cancelling_ state;
     *        when `false` then the [handler] is invoked only when it transitions to _completed_ state.
     * @param invokeImmediately when `true` and this job is already in the desired state (depending on [onCancelling]),
     *        then the [handler] is immediately and synchronously invoked and no-op [DisposableHandle] is returned;
     *        when `false` then no-op [DisposableHandle] is returned, but the [handler] is not invoked.
     * @param handler the handler.
     * */

        job.invokeOnCompletion(
            onCancelling = true, //handler will run even if the job is cancelled
            invokeImmediately = true,
            handler = object : CompletionHandler {
                //Registers handler that is **synchronously** invoked once on CANCELLATION or COMPLETION of this job.
                //what will happens when the job goes to completion or cancellation state
                override fun invoke(cause: Throwable?) {
                    if (job.isCancelled) {
                        Log.e(TAG, "NetworkBoundResource: Job has been cancelled...")
                        //if this handler was triggered because the job was cancelled (more than 3secds delay)...then return error
                        cause?.let {
                            onErrorReturn(it.message, false, true)
                        } ?: onErrorReturn(ERROR_UNKNOWN, false, true)
                    } else if (job.isCompleted) {
                        //if this handler was triggered because the job was completed...then do nothing
                        Log.e(TAG, "NetworkBoundResource: Job has been completed...")
                    }
                }
            })
        coroutineScope = CoroutineScope(IO + job)
        /*
        * coroutineScope = CoroutineScope(IO) -> to cancel this coroutine we have to write coroutine.cancel()
        * but this will cancel all coroutine within this scope
        *
        * coroutineScope = CoroutineScope(IO+job) -> job.cancel() will cancel only the coroutine within this scope
        * that have this job
        *
        * each transaction will have its own job
        * */
        return job
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>
    abstract suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)
    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>
    abstract fun setJob(job: Job) //keep track of the job
}