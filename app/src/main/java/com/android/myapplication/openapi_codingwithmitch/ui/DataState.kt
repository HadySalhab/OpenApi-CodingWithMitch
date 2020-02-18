package com.android.myapplication.openapi_codingwithmitch.ui

data class DataState<T>(
        var loading:Loading = Loading(false), //loading is not an event, because if it is loading and the user rotates, we still want show him that it is loading (if it is the case)
        var error:Event<StateError>?=null, //Error is an event and can be looked once
        var data:Data<T>?=null //data is an event also because  both of its field are wrapped in Event
){

    companion object{
        /*
        *
        * Factory methods
        * */

        fun<T> error(response:Response):DataState<T>{
            return DataState(Loading(false),Event<StateError>(StateError(response)),null)
        }
        fun <T> loading(isLoading:Boolean,cachedData:T?=null):DataState<T>{
            return DataState(Loading(isLoading),null,Data(Event.dataEvent(cachedData),null))
        }
        fun<T> data(data:T?=null,response:Response?=null):DataState<T>{
            return DataState(Loading(false),null,Data(Event.dataEvent(data),Event.responseEvent(response)))
        }
    }
}