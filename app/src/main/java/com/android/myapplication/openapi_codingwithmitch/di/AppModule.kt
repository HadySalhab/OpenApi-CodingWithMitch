package com.android.myapplication.openapi_codingwithmitch.di

import android.app.Application
import androidx.room.Room
import com.android.myapplication.openapi_codingwithmitch.R
import com.android.myapplication.openapi_codingwithmitch.persistence.AccountPropertiesDao
import com.android.myapplication.openapi_codingwithmitch.persistence.AppDatabase
import com.android.myapplication.openapi_codingwithmitch.persistence.AppDatabase.Companion.DATABASE_NAME
import com.android.myapplication.openapi_codingwithmitch.persistence.AuthTokenDao
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule{

    @Singleton
    @Provides
    fun provideAppDb(app: Application): AppDatabase {
        return Room
            .databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration() // get correct db version if schema changed
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthTokenDao(db: AppDatabase): AuthTokenDao {
        return db.getAuthTokenDao()
    }

    @Singleton
    @Provides
    fun provideAccountPropertiesDao(db: AppDatabase): AccountPropertiesDao {
        return db.getAccountPropertiesDao()
    }

    @Singleton
    @Provides
    fun provideRequestOptions(): RequestOptions {
        return RequestOptions
            .placeholderOf(R.drawable.default_image)
            .error(R.drawable.default_image)
    }

    @Singleton
    @Provides
    fun provideGlideInstance(application: Application, requestOptions: RequestOptions): RequestManager {
        return Glide.with(application)
            .setDefaultRequestOptions(requestOptions)
    }

}