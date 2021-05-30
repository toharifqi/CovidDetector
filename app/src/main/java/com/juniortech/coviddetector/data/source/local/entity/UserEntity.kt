package com.juniortech.coviddetector.data.source.local.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userEntities")
data class UserEntity (
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "userId")
    var userId: String,

    @ColumnInfo(name = "name")
    var userName: String,

    @ColumnInfo(name = "email")
    var userEmail: String,

    @ColumnInfo(name = "photo")
    var userPhoto: String,

    @ColumnInfo(name = "idCard")
    var userIdCard: String,

    @ColumnInfo(name = "phone")
    var userPhone: String,

    @ColumnInfo(name = "address")
    var userAddress: String,

    @ColumnInfo(name = "status")
    var userStatus: Int

)