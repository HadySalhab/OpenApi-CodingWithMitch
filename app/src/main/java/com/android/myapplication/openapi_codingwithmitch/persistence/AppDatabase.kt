package com.android.myapplication.openapi_codingwithmitch.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.myapplication.openapi_codingwithmitch.models.auth.AccountProperties
import com.android.myapplication.openapi_codingwithmitch.models.auth.AuthToken
import com.android.myapplication.openapi_codingwithmitch.persistence.auth.AccountPropertiesDao
import com.android.myapplication.openapi_codingwithmitch.persistence.auth.AuthTokenDao

@Database(entities = [AuthToken::class, AccountProperties::class], version = 1, exportSchema = true)
abstract class AppDatabase :RoomDatabase() {
    abstract fun getAuthTokenDao(): AuthTokenDao
    abstract fun getAccountPropertiesDao(): AccountPropertiesDao
    companion object{
        const val DATABASE_NAME = "app_db"
    }
}