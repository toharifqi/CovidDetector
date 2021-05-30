package com.juniortech.coviddetector.di

import android.content.Context
import com.juniortech.coviddetector.data.CovidRepository
import com.juniortech.coviddetector.data.source.local.LocalDataSource
import com.juniortech.coviddetector.data.source.local.room.CovidDatabase
import com.juniortech.coviddetector.data.source.remote.RemoteDataSource
import com.juniortech.coviddetector.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): CovidRepository{
        val database = CovidDatabase.getInstance(context)

        val remoteDataSource = RemoteDataSource.getInstance(context)
        val localDataSource = LocalDataSource.getInstance(database.covidDao())
        val appExecutors = AppExecutors()
        return  CovidRepository.getInstance(remoteDataSource, localDataSource, appExecutors)
    }
}