package com.android.myapplication.openapi_codingwithmitch.ui

import com.android.myapplication.openapi_codingwithmitch.ui.common.DataState

interface DataStateChangeListener{
    fun onDataStateChange(dataState: DataState<*>?)
}