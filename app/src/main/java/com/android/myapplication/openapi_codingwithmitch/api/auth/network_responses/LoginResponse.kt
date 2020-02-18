package com.android.myapplication.openapi_codingwithmitch.api.auth.network_responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginResponse(

    //Always returned
    @SerializedName("response")
    @Expose
    var response: String,

    //only on error
    //error here is not ApiErrorResponse
    //instead it is Actually ApiSuccessResponse but with some error like:
    //wrong password or email is invalid
    @SerializedName("error_message")
    @Expose
    var errorMessage: String,

    /*
    * All Below will be returned when success
    * */
    @SerializedName("token")
    @Expose
    var token: String,

    @SerializedName("pk")
    @Expose
    var pk: Int,

    @SerializedName("email")
    @Expose
    var email: String
)
{
    override fun toString(): String {
        return "LoginResponse(response='$response', errorMessage='$errorMessage', token='$token', pk=$pk, email='$email')"
    }
}
