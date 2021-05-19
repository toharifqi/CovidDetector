package com.juniortech.coviddetector.data.source.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.juniortech.coviddetector.data.source.local.entity.UserEntity

@Dao
interface CovidDao {

    @Query("SELECT * FROM userEntities WHERE userId = :userId")
    fun getUserById(userId: String): LiveData<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity)

    @Update
    fun updateUser(user: UserEntity)
}