package com.juniortech.coviddetector.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.juniortech.coviddetector.data.CovidRepository
import com.juniortech.coviddetector.di.Injection
import com.juniortech.coviddetector.ui.authentication.AuthViewModel
import com.juniortech.coviddetector.ui.profile.ProfileViewModel

class ViewModelFactory private constructor(private val covidRepository: CovidRepository): ViewModelProvider.NewInstanceFactory(){
    companion object{
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this){
                ViewModelFactory(Injection.provideRepository(context)).apply {
                    instance = this
                }
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when{
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                return ProfileViewModel(covidRepository) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                return AuthViewModel(covidRepository) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
    }
}