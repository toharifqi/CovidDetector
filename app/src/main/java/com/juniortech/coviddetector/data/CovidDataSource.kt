package com.juniortech.coviddetector.data

import androidx.lifecycle.LiveData
import com.juniortech.coviddetector.data.source.local.entity.UserEntity
import com.juniortech.coviddetector.data.source.remote.response.UserResponse
import com.juniortech.coviddetector.vo.Resource

interface CovidDataSource {
    fun getUserById(userId: String): LiveData<Resource<UserEntity>>

    fun addNewUser(userId: String, user: UserResponse)
}