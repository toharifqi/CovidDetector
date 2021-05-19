package com.juniortech.coviddetector.data.source.local

import com.juniortech.coviddetector.data.source.local.entity.UserEntity
import com.juniortech.coviddetector.data.source.local.room.CovidDao

class LocalDataSource private constructor(private val mCovidDao: CovidDao){
    companion object{
        private var INSTANCE: LocalDataSource? = null
        fun getInstance(covidDao: CovidDao): LocalDataSource =
            INSTANCE ?: LocalDataSource(covidDao)
    }

    fun getUserById(userId: String) = mCovidDao.getUserById(userId)

    fun insertUser(user: UserEntity) = mCovidDao.insertUser(user)

    fun updateUser(user: UserEntity) = mCovidDao.updateUser(user)
}