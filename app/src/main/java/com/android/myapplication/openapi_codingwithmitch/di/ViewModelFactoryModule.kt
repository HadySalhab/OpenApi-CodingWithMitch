package com.android.myapplication.openapi_codingwithmitch.di

import androidx.lifecycle.ViewModelProvider
import com.android.myapplication.openapi_codingwithmitch.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}