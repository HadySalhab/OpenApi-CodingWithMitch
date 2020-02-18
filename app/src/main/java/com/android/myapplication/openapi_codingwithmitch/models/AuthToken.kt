package com.android.myapplication.openapi_codingwithmitch.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "auth_token",
    foreignKeys = [
        ForeignKey(
            entity = AccountProperties::class,
            parentColumns = ["pk"],
            childColumns = ["account_pk"],
            onDelete = CASCADE //if the row in the parent table is deleted, we want to delete the child as well
        )
    ]
)
//child for the Account properties table
data class AuthToken(
    @PrimaryKey
    @ColumnInfo(name = "account_pk")
    //-1 value here is just to make sure if the foreign key is working
    var account_pk: Int? = -1, //this variable is the foreign key, refering to the pk in the Account properties

    @SerializedName("token")
    @Expose
    @ColumnInfo(name = "token")
    var token: String? = null //can be null, when it is null it means you are not authenticated.
)