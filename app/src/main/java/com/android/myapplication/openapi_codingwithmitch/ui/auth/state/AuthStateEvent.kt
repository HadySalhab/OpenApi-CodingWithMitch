package com.android.myapplication.openapi_codingwithmitch.ui.auth.state

sealed class AuthStateEvent {
    /*
    * Different type of AuthStateEvent
    * We Have:
    * login event
    * register event
    * check previous event (because if token is available in database, we want to skip the login/register event)
    *
    * each event should pass with the it the required parameter
    * */
    data class LoginAttemptEvent(
        val email: String,
        val password: String
    ) : AuthStateEvent()

    data class RegisterAttemptEvent(
        val email: String,
        val username: String,
        val password: String,
        val confirm_password: String
    ) : AuthStateEvent()

    class CheckPreviousAuthEvent() : AuthStateEvent()
}