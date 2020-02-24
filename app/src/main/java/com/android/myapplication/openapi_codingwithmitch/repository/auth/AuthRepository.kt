package com.android.myapplication.openapi_codingwithmitch.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.android.myapplication.openapi_codingwithmitch.api.auth.OpenApiAuthService
import com.android.myapplication.openapi_codingwithmitch.api.auth.network_responses.LoginResponse
import com.android.myapplication.openapi_codingwithmitch.api.auth.network_responses.RegistrationResponse
import com.android.myapplication.openapi_codingwithmitch.models.auth.AccountProperties
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
import com.android.myapplication.openapi_codingwithmitch.util.AbsentLiveData
import com.android.myapplication.openapi_codingwithmitch.util.ApiSuccessResponse
import com.android.myapplication.openapi_codingwithmitch.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.android.myapplication.openapi_codingwithmitch.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.android.myapplication.openapi_codingwithmitch.util.GenericApiResponse
import com.android.myapplication.openapi_codingwithmitch.util.PreferenceKeys
import com.android.myapplication.openapi_codingwithmitch.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.coroutines.Job

class AuthRepository constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor:SharedPreferences.Editor

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
            sessionManager.isConnectedToTheInternet(),true
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



                //because of a foreign key relationship between AuthToken And AccountProperties
                //we have to make sure that the AccountProp exists before interacting with AuthToken table
                //if the user account properties already exist, then ignore this process
                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        ""
                    )
                )

                //will return -1 if failure
                val result = authTokenDao.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )

                //still not sure why would be an error to save the authToken
                if (result<0){
                    onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN,ResponseType.Dialog())
                        ))
                    return

                }

                //we are saving the authenticated user to sharedPreferences for future login
                saveAuthenticatedUserToPreferences(email)


                //here we have a normal success response
                //this will update the mediatorlivedata under the hood....
                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            //registration and login fields are not parameters that the authviewState absorbs from the api call
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

            override suspend fun createCacheRequestAndReturn() {
                //N/A
            }

        }.asLiveData()

    }


    fun checkPreviousAuthUser():LiveData<DataState<AuthViewState>>{
        val previousAuthUserEmail:String? = sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER,null)

        //we are checking if there is an authenticated user in the shared prefrences
        if(previousAuthUserEmail.isNullOrBlank()){
            Log.d(TAG, "checkPreviousAuthUser: No previously Authenticated user found...")
            return returnNoTokenFound()
        }

        //at this stage, we have an authenticated user in the sharedPreferences.but we still need to check the authToken
        return object :NetworkBoundResource<Void,AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            false
        ){
            override suspend fun createCacheRequestAndReturn() {
                //we are getting the account properties of the authenticated user email
                accountPropertiesDao.searchByEmail(previousAuthUserEmail).let { accountProperties ->
                    Log.d(TAG, "createCacheRequestAndReturn: searching for token $accountProperties")
                    accountProperties?.let {
                        //-1 is just a reference, to check if the foreign key is working
                        if(accountProperties.pk>-1){
                            //we are getting the authToken using account properties pk
                            authTokenDao.searchByPk(accountProperties.pk).let { authToken ->
                                //checking if the user didn't logout
                                //logging out will nullify the token in the db
                                if(authToken !=null){
                                    //success, we got everything we need
                                    onCompleteJob(DataState.data(
                                        AuthViewState(
                                        authToken = authToken
                                    )))
                                    return
                                }
                            }
                        }
                    }
                    //we couldn't find the authToken here
                    Log.d(TAG, "createCacheRequestAndReturn: AuthToken Not Found...")
                    onCompleteJob(DataState.data(
                        data = null,
                        response = Response(
                            RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                            ResponseType.None()
                        )
                    ))
                }
            }

            //not used in this case
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Void>) {
            }

            //not used in this case
            override fun createCall(): LiveData<GenericApiResponse<Void>> {
                return AbsentLiveData.create()
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob=job
            }

        }.asLiveData()
    }

    private fun returnNoTokenFound(): LiveData<DataState<AuthViewState>> {
        return object:LiveData<DataState<AuthViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.data(
                    data=null, //because the data is null -> in the AuthActivity, if the data is null Nothing will happen
                    response = Response(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,ResponseType.None())
                )
            }
        }
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
            sessionManager.isConnectedToTheInternet(),true
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


                //because of a foreign key relationship between AuthToken And AccountProperties
                //we have to make sure that the AccountProp exists before interacting with AuthToken table
                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        ""
                    )
                )

                //will return -1 if failure
                val result = authTokenDao.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )
                //if we couldn't save the user token , then return and complete job
                if (result<0){
                    onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN,ResponseType.Dialog())
                        ))
                    return

                }

                //at this stage everything is fine and we can proceed to save the authenticated user
                saveAuthenticatedUserToPreferences(email)



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

            override suspend fun createCacheRequestAndReturn() {
                //N/A
            }

        }.asLiveData()
    }


    private fun saveAuthenticatedUserToPreferences(email:String){
        sharedPrefsEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER,email)
        sharedPrefsEditor.apply()
    }

}