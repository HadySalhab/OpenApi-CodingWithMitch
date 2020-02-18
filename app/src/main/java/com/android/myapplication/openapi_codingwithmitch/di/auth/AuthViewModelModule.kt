package com.android.myapplication.openapi_codingwithmitch.di.auth

import androidx.lifecycle.ViewModel
import com.android.myapplication.openapi_codingwithmitch.di.ViewModelKey
import com.android.myapplication.openapi_codingwithmitch.ui.auth.viewmodel.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel

}