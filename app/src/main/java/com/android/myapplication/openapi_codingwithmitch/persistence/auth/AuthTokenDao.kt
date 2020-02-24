package com.android.myapplication.openapi_codingwithmitch.persistence.auth

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.myapplication.openapi_codingwithmitch.models.auth.AuthToken

@Dao
interface AuthTokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(authToken: AuthToken):Long

    //account_pk is the foreign key
    //when we logout, we want to set the token to null (we are not deleting the row)
    @Query("UPDATE auth_token SET token=null WHERE account_pk = :pk")
    fun nullifyToken(pk:Int):Int

    //account_pk is the foreign key
    @Query("SELECT * FROM auth_token WHERE account_pk = :pk")
    suspend fun searchByPk(pk: Int): AuthToken?


}