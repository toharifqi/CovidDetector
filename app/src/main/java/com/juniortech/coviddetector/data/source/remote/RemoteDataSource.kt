package com.juniortech.coviddetector.data.source.remote

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.juniortech.coviddetector.data.source.remote.response.UserResponse

class RemoteDataSource private constructor(context: Context){
    private val userDb: DatabaseReference = FirebaseDatabase.getInstance(DATABASE_URL).getReference("users")

    companion object{
        const val DATABASE_URL = "https://capstone-project-junior-tech-default-rtdb.asia-southeast1.firebasedatabase.app/"

        @Volatile
        private var instance: RemoteDataSource? = null
        fun getInstance(context: Context): RemoteDataSource =
            instance ?: synchronized(this){
                instance ?: RemoteDataSource(context).apply { instance = this }
            }
    }

    fun getUserById(userId: String): LiveData<ApiResponse<UserResponse>>{
        val resultUser = MutableLiveData<ApiResponse<UserResponse>>()
        userDb.child(userId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userIdRemote = snapshot.child("userId").value.toString()
                val userName = snapshot.child("userName").value.toString()
                val userEmail = snapshot.child("userEmail").value.toString()
                val userPhoto = snapshot.child("userPhoto").value.toString()
                val userIdCard = snapshot.child("userIdCard").value.toString()
                val userPhone = snapshot.child("userPhone").value.toString()
                val userAddress = snapshot.child("userAddress").value.toString()
                val userStatus = snapshot.child("userStatus").value.toString().toInt()
                val userResponse = UserResponse(userIdRemote, userName, userEmail, userPhoto, userIdCard, userPhone,
                userAddress, userStatus)

                resultUser.value = ApiResponse.success(userResponse)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        return resultUser
    }

    fun addNewUser(userId: String, user: UserResponse){
        userDb.child(userId).setValue(user)
    }
}