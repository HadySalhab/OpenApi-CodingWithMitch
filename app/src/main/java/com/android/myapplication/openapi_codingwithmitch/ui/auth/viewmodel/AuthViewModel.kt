package com.android.myapplication.openapi_codingwithmitch.ui.auth.viewmodel

import androidx.lifecycle.LiveData
import com.android.myapplication.openapi_codingwithmitch.models.auth.AuthToken
import com.android.myapplication.openapi_codingwithmitch.repository.auth.AuthRepository
import com.android.myapplication.openapi_codingwithmitch.ui.auth.state.AuthStateEvent
import com.android.myapplication.openapi_codingwithmitch.ui.auth.state.AuthStateEvent.*
import com.android.myapplication.openapi_codingwithmitch.ui.auth.state.AuthViewState
import com.android.myapplication.openapi_codingwithmitch.ui.auth.state.LoginFields
import com.android.myapplication.openapi_codingwithmitch.ui.auth.state.RegistrationFields
import com.android.myapplication.openapi_codingwithmitch.ui.common.BaseViewModel
import com.android.myapplication.openapi_codingwithmitch.ui.common.DataState
import javax.inject.Inject

//this class will live as long as the authActivity is not FINISHED
//because its context is the authactivity
class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
):BaseViewModel<AuthStateEvent,AuthViewState>(){


    //will be triggered whenever the stateEvent is changed.
    //will return Livedata to the dataState
    //the datastate will change the viewstate
    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        //state event is a sealed class
        when(stateEvent){
            //are we trying to login?
            is LoginAttemptEvent->{
                return authRepository.attemptLogin(stateEvent.email,stateEvent.password)
            }
            //are we trying to register?
            is RegisterAttemptEvent->{
                return authRepository.attemptRegistration(
                    stateEvent.email,
                    stateEvent.username,
                    stateEvent.password,
                    stateEvent.confirm_password
                )
            }
            //are we checking previous authentication?
            is CheckPreviousAuthEvent->{
                return authRepository.checkPreviousAuthUser()
            }
        }
    }


    /*
    * Setter for each viewstate field
    * will update the viewState object in the liveData
    * */
    fun setRegistrationFields(registrationFields: RegistrationFields){
        //this will either return the value of the LiveData<ViewState> or will create a new ViewState instance
        val currentViewStateOrNew = getCurrentViewStateOrNew()
        if(currentViewStateOrNew.registrationFields == registrationFields){
            return
        }
        //updating the field
        currentViewStateOrNew.registrationFields = registrationFields
        //this will update the value in the livedata viewstate
        _viewState.value = currentViewStateOrNew
    }
    fun setLoginFields(loginFields: LoginFields){
        //this will either return the value of the LiveData<ViewState> or will create a new ViewState instance
        val currentViewStateOrNew = getCurrentViewStateOrNew()
        if(currentViewStateOrNew.loginFields == loginFields){
            return
        }
        //updating the field
        currentViewStateOrNew.loginFields = loginFields
        //this will update the value in the livedata viewstate
        _viewState.value = currentViewStateOrNew
    }

    fun setAuthToken(authToken: AuthToken){
        //this will either return the value of the LiveData<ViewState> or will create a new ViewState instance
        val currentViewStateOrNew = getCurrentViewStateOrNew()
        if(currentViewStateOrNew.authToken == authToken){
            return
        }
        //updating the field
        currentViewStateOrNew.authToken = authToken
        //this will update the value in the livedata viewstate
        _viewState.value = currentViewStateOrNew
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }

    fun cancelActiveJobs(){
        authRepository.cancelActiveJob()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}