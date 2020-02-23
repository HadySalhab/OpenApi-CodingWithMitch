package com.android.myapplication.openapi_codingwithmitch.ui

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.MaterialDialog
import com.android.myapplication.openapi_codingwithmitch.R

fun Context.displayToast(@StringRes message:Int){
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
}

fun Context.displayToast(message:String){
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
}


fun Context.displaySuccesDialog(message:String?){
    //third party library
    MaterialDialog(this).show {
        title(R.string.text_success)
        message(text=message)
        positiveButton (R.string.text_ok)
    }
}

fun Context.displayErrorDialog(message:String?){
    //third pardy library
    MaterialDialog(this).show {
        title(R.string.text_error)
        message(text=message)
        positiveButton (R.string.text_ok)
    }
}
