package com.android.myapplication.openapi_codingwithmitch.repository.auth

import android.util.Log
import androidx.lifecycle.LiveData
import com.android.myapplication.openapi_codingwithmitch.api.auth.OpenApiAuthService
import com.android.myapplication.openapi_codingwithmitch.api.auth.network_responses.LoginResponse
import com.android.myapplication.openapi_codingwithmitch.api.auth.network_responses.RegistrationResponse
import com.android.myapplication.openapi_codingwithmitch.models.auth.AuthToken
import com.android.myapplication.openapi_codingwithmitch.persistence.auth.AccountPropertiesDao
import com.android.myapplication.openapi_codingwithmitch.persistence.auth.AuthTokenDao
import com.android.myapplication.openapi_codingwithmitch.repository.NetworkBoundResource
import com.android.myapplication.openapi_codingwithmitch.session.SessionManager
import com.android.myapplication.openapi_codingwithmitch.ui.auth.state.AuthViewState
import com.android.myapplication.openapi_codingwithmitch.ui.auth.state.LoginFields
import com.android.myapplication.openapi_codingwithmitch.ui.auth.state.RegistrationFields
import com.android.myapplication.openapi_codingwithmitch.ui.common.DataState
import com.android.myapplication.openapi_codingwithmitch.ui.common.Response
import com.android.myapplication.openapi_codingwithmitch.ui.common.ResponseType
import com.android.myapplication.openapi_codingwithmitch.util.ApiSuccessResponse
import com.android.myapplication.openapi_codingwithmitch.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.android.myapplication.openapi_codingwithmitch.util.GenericApiResponse
import kotlinx.coroutines.Job

class AuthRepository constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
) {
    companion object {
        private const val TAG = "AuthRepository"
    }

    private var repositoryJob
            : Job? = null

    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        //first of all we are checking if the fields are valid at the client side
        val loginFieldErrors = LoginFields(email, password).isValidForLogin() //this will return a string
        //if the message is not none , it means that we have an error at the client side, might be email empty or password empty
        if (!loginFieldErrors.equals(LoginFields.LoginError.none()))//none() returns "none"
        {
            return returnErrorResponse(loginFieldErrors, ResponseType.Dialog())
        }


        //at this stage, at the client side we dont have any problem , so we can proceed
        return object : NetworkBoundResource<LoginResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet()
        ) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                //Incorrect login credentials counts as a 200 response from the server , so we have to handle that
                if (response.body.response.equals(GENERIC_AUTH_ERROR)) { //THIS CONDITION IS SPECIFIC TO THE API
                    onErrorReturn(
                        response.body.errorMessage,
                        true,
                        false
                    ) //this will update the mediatorlivedata under the hood.
                    return
                }
                //here we have a normal success response
                //this will update the mediatorlivedata under the hood....
                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(email, password)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job //now we have access to the job that is running our networkbound resources

            }

        }.asLiveData()

    }
    fun cancelActiveJob(){
        Log.d(TAG, "AuthRepository:Cancelling on-goin jobs...")
        repositoryJob?.cancel()
    }

    private fun returnErrorResponse(
        errorMessage: String,
        responseType: ResponseType
    ): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    Response(
                        errorMessage,
                        responseType
                    )
                )
            }
        }
    }




    fun attemptRegistration(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    )
    //we want to return DataState object which its generic param type (data) is AuthViewState
            : LiveData<DataState<AuthViewState>> {
        //first check the client side fields
        val registrationFieldsErrors = RegistrationFields(email,username,password,confirmPassword).isValidForRegistration()
        if(!registrationFieldsErrors.equals(RegistrationFields.RegistrationError.none())) {
            return returnErrorResponse(registrationFieldsErrors,ResponseType.Dialog())
        }
        return object :NetworkBoundResource<RegistrationResponse,AuthViewState>(
            sessionManager.isConnectedToTheInternet()
        ){
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")
                //Incorrect login credentials counts as a 200 response from the server , so we have to handle that
                if(response.body.response.equals(GENERIC_AUTH_ERROR)){
                    onErrorReturn(
                        response.body.errorMessage,
                        true,
                        false
                    ) //this will update the mediatorlivedata under the hood.
                    return
                }
                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                return openApiAuthService.register(email,username,password,confirmPassword)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

        }.asLiveData()
    }

}