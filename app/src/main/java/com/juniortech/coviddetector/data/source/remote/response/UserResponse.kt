package com.juniortech.coviddetector.data.source.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserResponse (
    val userId: String,
    val userName: String,
    val userEmail: String,
    val userPhoto: String,
    val userIdCard: String,
    val userPhone: String,
    val userAddress: String,
    val userStatus: Int
) : Parcelable