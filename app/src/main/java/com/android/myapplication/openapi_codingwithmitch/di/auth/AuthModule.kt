package com.android.myapplication.openapi_codingwithmitch.di.auth

import com.android.myapplication.openapi_codingwithmitch.api.auth.OpenApiAuthService
import com.android.myapplication.openapi_codingwithmitch.persistence.auth.AccountPropertiesDao
import com.android.myapplication.openapi_codingwithmitch.persistence.auth.AuthTokenDao
import com.android.myapplication.openapi_codingwithmitch.repository.auth.AuthRepository
import com.android.myapplication.openapi_codingwithmitch.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthModule{

    // TEMPORARY
    @AuthScope
    @Provides
    fun provideFakeApiService(retrofitBuilder:Retrofit.Builder): OpenApiAuthService {
        return retrofitBuilder.build().create(OpenApiAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService
        ): AuthRepository {
        return AuthRepository(
            authTokenDao,
            accountPropertiesDao,
            openApiAuthService,
            sessionManager
        )
    }

}