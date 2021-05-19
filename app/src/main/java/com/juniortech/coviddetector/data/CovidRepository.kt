package com.juniortech.coviddetector.data

import androidx.lifecycle.LiveData
import com.juniortech.coviddetector.data.source.local.LocalDataSource
import com.juniortech.coviddetector.data.source.local.entity.UserEntity
import com.juniortech.coviddetector.data.source.remote.ApiResponse
import com.juniortech.coviddetector.data.source.remote.RemoteDataSource
import com.juniortech.coviddetector.data.source.remote.response.UserResponse
import com.juniortech.coviddetector.utils.AppExecutors
import com.juniortech.coviddetector.vo.Resource

class CovidRepository private constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors
): CovidDataSource {

    companion object{
        @Volatile
        private var instance: CovidRepository? = null

        fun getInstance(remoteData: RemoteDataSource, localData: LocalDataSource, appExecutors: AppExecutors): CovidRepository =
            instance ?: synchronized(this){
                instance ?: CovidRepository(remoteData, localData, appExecutors).apply { instance = this }
            }
    }

    override fun getUserById(userId: String): LiveData<Resource<UserEntity>> {
        return object : NetworkBoundResource<UserEntity, UserResponse>(appExecutors){
            override fun loadFromDB(): LiveData<UserEntity> =
                localDataSource.getUserById(userId)

            override fun shouldFetch(data: UserEntity?): Boolean =
                data?.userId == null

            override fun createCall(): LiveData<ApiResponse<UserResponse>> =
                remoteDataSource.getUserById(userId)

            override fun saveCallResult(data: UserResponse) {
                val user = UserEntity(
                    userId = data.userId,
                    userEmail = data.userEmail,
                    userName = data.userName,
                    userIdCard = data.userIdCard,
                    userPhone = data.userPhone,
                    userAddress = data.userPhone,
                    userStatus = data.userStatus
                )
                localDataSource.insertUser(user)
            }
        }.asLiveData()
    }

    override fun addNewUser(userId: String, user: UserResponse) {
        remoteDataSource.addNewUser(userId, user)
    }


}