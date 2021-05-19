package com.juniortech.coviddetector.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.juniortech.coviddetector.data.source.remote.response.UserResponse

class RemoteDataSource {
    private lateinit var userDb: DatabaseReference

    companion object{
        const val DATABASE_URL = "https://capstone-project-junior-tech-default-rtdb.asia-southeast1.firebasedatabase.app/"

        @Volatile
        private var instance: RemoteDataSource? = null
        fun getInstance(): RemoteDataSource =
            instance ?: synchronized(this){
                instance ?: RemoteDataSource().apply { instance = this }
            }
    }

    fun getUserById(userId: String): LiveData<ApiResponse<UserResponse>>{
        val resultUser = MutableLiveData<ApiResponse<UserResponse>>()
        userDb = FirebaseDatabase.getInstance(DATABASE_URL).getReference("users").child(userId)

        userDb.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userIdRemote = snapshot.child("userId").value.toString()
                val userName = snapshot.child("userName").value.toString()
                val userEmail = snapshot.child("userEmail").value.toString()
                val userIdCard = snapshot.child("userIdCard").value.toString()
                val userPhone = snapshot.child("userPhone").value.toString()
                val userAddress = snapshot.child("userAddress").value.toString()
                val userStatus = snapshot.child("userStatus").value.toString().toInt()
                val userResponse = UserResponse(userIdRemote, userName, userEmail, userIdCard, userPhone,
                userAddress, userStatus)

                resultUser.value = ApiResponse.success(userResponse)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        return resultUser
    }

    fun addNewUser(userId: String, user: UserResponse){
        userDb = FirebaseDatabase.getInstance(DATABASE_URL).getReference("users")
        userDb.child(userId).setValue(user)
    }
}