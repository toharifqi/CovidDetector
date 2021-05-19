package com.juniortech.coviddetector.ui.authentication

import androidx.lifecycle.ViewModel
import com.juniortech.coviddetector.data.CovidRepository
import com.juniortech.coviddetector.data.source.remote.response.UserResponse

class AuthViewModel(private val covidRepository: CovidRepository) : ViewModel() {
    fun addNewUser(userId: String, userResponse: UserResponse){
        covidRepository.addNewUser(userId, userResponse)
    }
}