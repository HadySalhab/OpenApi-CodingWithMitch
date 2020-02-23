package com.android.myapplication.openapi_codingwithmitch.ui.auth.state

import com.android.myapplication.openapi_codingwithmitch.models.auth.AuthToken


//we split the fields into classes here because multiple fragments(views) are sharing the same viewModel
//and each viewModel has one viewState
data class AuthViewState(
    var registrationFields: RegistrationFields? = RegistrationFields(),//belongs to register fragment
    var loginFields: LoginFields? = LoginFields(), //belongs to login fragment
    var authToken: AuthToken? = null //we need it to capture the response from the api so we send it back to the SessionManager
)





data class RegistrationFields(
    var registration_email: String? = null,
    var registration_username: String? = null,
    var registration_password: String? = null,
    var registration_confirm_password: String? = null
){
    //handle registration error on the client side
    fun isValidForRegistration(): String{
        if(registration_email.isNullOrEmpty()
            || registration_username.isNullOrEmpty()
            || registration_password.isNullOrEmpty()
            || registration_confirm_password.isNullOrEmpty()){
            return RegistrationError.mustFillAllFields()
        }

        if(!registration_password.equals(registration_confirm_password)){
            return RegistrationError.passwordsDoNotMatch()
        }
        return RegistrationError.none()
    }

    //helper class that includes all the error that can happen on the client side
    class RegistrationError {
        companion object{

            fun mustFillAllFields(): String{
                return "All fields are required."
            }

            fun passwordsDoNotMatch(): String{
                return "Passwords must match."
            }

            fun none():String{
                return "None"
            }

        }

    }

}


data class LoginFields(
    var login_email: String? = null,
    var login_password: String? = null
){
    //handle registration error on the client side
    fun isValidForLogin(): String{

        if(login_email.isNullOrEmpty()
            || login_password.isNullOrEmpty()){

            return LoginError.mustFillAllFields()
        }
        return LoginError.none()
    }

    //helper class that includes all the error that can happen on the client side for login
    class LoginError {

        companion object{

            fun mustFillAllFields(): String{
                return "You can't login without an email and password."
            }

            fun none():String{
                return "None"
            }

        }
    }

    override fun toString(): String {
        return "LoginState(email=$login_email, password=$login_password)"
    }
}