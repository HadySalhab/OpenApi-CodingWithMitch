package com.android.myapplication.openapi_codingwithmitch.api.auth.network_responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RegistrationResponse(

    @SerializedName("response")
    @Expose
    var response: String,

    //Error here does not mean ApiErrorResponse
    //the ApiResponse should Be successful to receive a serialized (error_message)
    //here error message is like:
    //Email Already in use
    //usernam already in use
    //password1 is different than password2
    @SerializedName("error_message")
    @Expose
    var errorMessage: String,

    @SerializedName("email")
    @Expose
    var email: String,

    @SerializedName("username")
    @Expose
    var username: String,

    @SerializedName("pk")
    @Expose
    var pk: Int,

    @SerializedName("token")
    @Expose
    var token: String)
{

    override fun toString(): String {
        return "RegistrationResponse(response='$response', errorMessage='$errorMessage', email='$email', username='$username', token='$token')"
    }
}