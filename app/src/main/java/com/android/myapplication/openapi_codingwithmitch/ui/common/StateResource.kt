package com.android.myapplication.openapi_codingwithmitch.ui.common


data class Loading(val isLoading: Boolean){}

//reponse here in success, to show success dialog for example
data class Data<T>(val data: Event<T>?, val response: Event<Response>?){}

data class StateError(val response: Response){}



data class Response(val message: String?, val responseType: ResponseType) //toast,dialogs,...
sealed class ResponseType {
    class Toast : ResponseType()
    class Dialog : ResponseType()
    class None : ResponseType()
}


//Generic class that can be used in any project
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */

    //We want to look at the event only once
    //return its generic param
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content

    override fun toString(): String {
        return "Event(content=$content, hasBeenHandled=$hasBeenHandled)"
    }

    /*
    * Factory Methods
    *
    * */
    companion object {

        private val TAG: String = "AppDebug"


        // we don't want an event if the data is null
        fun <T> dataEvent(data: T?): Event<T>? {
            data?.let {
                return Event(
                    it
                )
            }
            return null
        }

        // we don't want an event if the response is null
        fun responseEvent(response: Response?): Event<Response>? {
            response?.let {
                return Event(
                    response
                )
            }
            return null
        }
    }
}
