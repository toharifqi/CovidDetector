package com.juniortech.coviddetector.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.juniortech.coviddetector.data.CovidRepository
import com.juniortech.coviddetector.data.source.local.entity.UserEntity
import com.juniortech.coviddetector.data.source.remote.response.UserResponse
import com.juniortech.coviddetector.vo.Resource

class ProfileViewModel(private val covidRepository: CovidRepository) : ViewModel() {
    fun user(uId: String): LiveData<Resource<UserEntity>> = covidRepository.getUserById(uId)
}