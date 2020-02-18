package com.android.myapplication.openapi_codingwithmitch.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.myapplication.openapi_codingwithmitch.models.AuthToken

@Dao
interface AuthTokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(authToken: AuthToken):Long

    //when we logout, we want to set the token to null (we are not deleting the row)
    @Query("UPDATE auth_token SET token=null WHERE account_pk = :pk")
    fun nullifyToken(pk:Int):Int


}