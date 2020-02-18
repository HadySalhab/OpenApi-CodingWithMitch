package com.android.myapplication.openapi_codingwithmitch.di.auth

import com.android.myapplication.openapi_codingwithmitch.ui.auth.fragments.child.ForgotPasswordFragment
import com.android.myapplication.openapi_codingwithmitch.ui.auth.fragments.child.LauncherFragment
import com.android.myapplication.openapi_codingwithmitch.ui.auth.fragments.child.LoginFragment
import com.android.myapplication.openapi_codingwithmitch.ui.auth.fragments.child.RegisterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeLauncherFragment(): LauncherFragment

    @ContributesAndroidInjector()
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector()
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector()
    abstract fun contributeForgotPasswordFragment(): ForgotPasswordFragment

}