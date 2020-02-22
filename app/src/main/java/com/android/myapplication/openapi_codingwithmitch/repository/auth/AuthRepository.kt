package com.android.myapplication.openapi_codingwithmitch.repository.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.android.myapplication.openapi_codingwithmitch.api.auth.OpenApiAuthService
import com.android.myapplication.openapi_codingwithmitch.models.auth.AuthToken
import com.android.myapplication.openapi_codingwithmitch.persistence.auth.AccountPropertiesDao
import com.android.myapplication.openapi_codingwithmitch.persistence.auth.AuthTokenDao
import com.android.myapplication.openapi_codingwithmitch.session.SessionManager
import com.android.myapplication.openapi_codingwithmitch.ui.auth.state.AuthViewState
import com.android.myapplication.openapi_codingwithmitch.ui.common.DataState
import com.android.myapplication.openapi_codingwithmitch.ui.common.Response
import com.android.myapplication.openapi_codingwithmitch.ui.common.ResponseType
import com.android.myapplication.openapi_codingwithmitch.util.ApiEmptyResponse
import com.android.myapplication.openapi_codingwithmitch.util.ApiErrorResponse
import com.android.myapplication.openapi_codingwithmitch.util.ApiSuccessResponse
import com.android.myapplication.openapi_codingwithmitch.util.ErrorHandling.Companion.ERROR_UNKNOWN

class AuthRepository constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
) {
    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        return openApiAuthService.login(email, password).switchMap { responseApi ->
            //we want to return DataState object which its generic param type (data) is AuthViewState
            object : LiveData<DataState<AuthViewState>>() {
                override fun onActive() {
                    super.onActive()
                    when (responseApi) {
                        is ApiSuccessResponse -> {
                            //creating the AuthViewState(data) from the response we got from the APi
                            value = DataState.data(
                                data = AuthViewState(authToken = AuthToken(responseApi.body.pk, responseApi.body.token)),
                                response = null
                            )
                        }
                        is ApiEmptyResponse -> {
                            value = DataState.error(
                                response = Response(message= ERROR_UNKNOWN,
                                    responseType = ResponseType.Dialog()
                                )
                            )

                        }
                        is ApiErrorResponse -> {
                            value = DataState.error(
                                response = Response(message= responseApi.errorMessage,
                                    responseType = ResponseType.Dialog()
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun attemptRegistration(
        email:String,
        username: String,
        password: String,
        confirmPassword:String)
            //we want to return DataState object which its generic param type (data) is AuthViewState
            : LiveData<DataState<AuthViewState>> {
        return openApiAuthService.register(email,username,password,confirmPassword).switchMap { responseApi ->
            object : LiveData<DataState<AuthViewState>>() {
                override fun onActive() {
                    super.onActive()
                    when (responseApi) {
                        is ApiSuccessResponse -> {
                            value = DataState.data(
                                //creating the AuthViewState(data) from the response we got from the APi
                                data = AuthViewState(authToken = AuthToken(responseApi.body.pk, responseApi.body.token)),
                                response = null
                            )
                        }
                        is ApiEmptyResponse -> {
                            value = DataState.error(
                                response = Response(message= ERROR_UNKNOWN,
                                    responseType = ResponseType.Dialog()
                                )
                            )

                        }
                        is ApiErrorResponse -> {
                            value = DataState.error(
                                response = Response(message= responseApi.errorMessage,
                                    responseType = ResponseType.Dialog()
                                )
                            )
                        }
                    }
                }
            }
        }
    }



}