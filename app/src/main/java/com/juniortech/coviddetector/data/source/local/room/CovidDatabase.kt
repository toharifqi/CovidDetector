package com.juniortech.coviddetector.data.source.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.juniortech.coviddetector.data.source.local.entity.UserEntity

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class CovidDatabase: RoomDatabase() {
    abstract fun covidDao(): CovidDao

    companion object{
        @Volatile
        private var INSTANCE: CovidDatabase? = null

        fun getInstance(context: Context): CovidDatabase =
            INSTANCE ?: synchronized(this){
                Room.databaseBuilder(
                    context.applicationContext,
                    CovidDatabase::class.java,
                    "Covid.db"
                ).build().apply {
                    INSTANCE = this
                }
            }
    }
}