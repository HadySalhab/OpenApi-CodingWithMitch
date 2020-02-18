package com.android.myapplication.openapi_codingwithmitch.di

import com.android.myapplication.openapi_codingwithmitch.di.auth.AuthFragmentBuildersModule
import com.android.myapplication.openapi_codingwithmitch.di.auth.AuthModule
import com.android.myapplication.openapi_codingwithmitch.di.auth.AuthScope
import com.android.myapplication.openapi_codingwithmitch.di.auth.AuthViewModelModule
import com.android.myapplication.openapi_codingwithmitch.ui.auth.AuthActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

}