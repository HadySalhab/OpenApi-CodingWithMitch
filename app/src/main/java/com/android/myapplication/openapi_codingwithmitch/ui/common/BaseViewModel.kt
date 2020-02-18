package com.android.myapplication.openapi_codingwithmitch.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

//All ViewModels in this application will extend this class
abstract class BaseViewModel<StateEvent, ViewState> : ViewModel() {
    val TAG: String = "AppDebug"

    protected val _stateEvent = MutableLiveData<StateEvent>()
    protected val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = _viewState

    //will be fired whenever the state event is changed
    val dataState: LiveData<DataState<ViewState>> = Transformations
        .switchMap(_stateEvent) { stateEvent ->
            stateEvent?.let {
                //when an event is triggered, we should handle it
                handleStateEvent(stateEvent)
            }
        }




    //this method will trigger an event
    fun setStateEvent(event: StateEvent) {
        _stateEvent.value = event
    }

    fun getCurrentViewStateOrNew(): ViewState {
        var value = viewState.value //Read
        return if (value != null) {
            value
        } else {
            value = initNewViewState() //write
            value
        }
    }
    abstract fun handleStateEvent(stateEvent: StateEvent): LiveData<DataState<ViewState>>
    abstract fun initNewViewState(): ViewState


}