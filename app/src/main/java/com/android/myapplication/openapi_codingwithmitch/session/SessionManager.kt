package com.android.myapplication.openapi_codingwithmitch.session

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.myapplication.openapi_codingwithmitch.models.auth.AuthToken
import com.android.myapplication.openapi_codingwithmitch.persistence.auth.AuthTokenDao
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(val authTokenDao: AuthTokenDao,
            val application: Application){

    private val TAG:String = "AppDebug"
    //AuthToken is a table that holds user primary key and the token
    //like a key-value pair, for each user pk , we have a token
    private val _cachedToken = MutableLiveData<AuthToken>()

    val cachedToken:LiveData<AuthToken>
    get() = _cachedToken


    //newValue is an object that holds the user pk and Token
    fun login (newValue:AuthToken){
        setValue(newValue)
    }


    //logout, will set the token to null in the session manager and the database
    fun logout(){
        Log.d(TAG, "logout: ...")
        //IO dispatcher, because we are connecting to the database
        GlobalScope.launch (IO){
            var errorMessage:String?=null
            try{
                cachedToken.value!!.account_pk?.let {
                    //we set the token to null in the database
                    authTokenDao.nullifyToken(it)
                }
                //cancellationException can be thrown by coroutine
            }catch (e:CancellationException){
                Log.e(TAG, "logout:${e.message} ")
                errorMessage = e.message
                //this to catch any random exception
            }catch(e:Exception){
                Log.e(TAG, "logout: ${e.message}")
                errorMessage = errorMessage + "\n" + e.message
            }
            //this block will always be called
            finally {
                errorMessage?.let {
                    Log.e(TAG, " logout: ${errorMessage}")
                }
                Log.d(TAG, "logout: finaly...")
                //set the token to null
                //because the user logged out
                //we nullify the token in the session manager as well
                setValue(null)
            }

        }
    }

    //we want the main thread here, because this might be called from a background thread
    // and livedata.setValue() can only be called from the main thread.
    fun setValue(newValue: AuthToken?){
        GlobalScope.launch(Main) {
            if(_cachedToken.value!=newValue){
                _cachedToken.value = newValue
            }
        }
    }

    //this method just check if the device is connected to the internet
    fun isConnectedToTheInternet():Boolean{
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try{
            return cm.activeNetworkInfo.isConnected
        }catch (e:java.lang.Exception){
            Log.e(TAG, "isConnectedToTheInternet: ${e.message}")
        }
        return false
    }
}