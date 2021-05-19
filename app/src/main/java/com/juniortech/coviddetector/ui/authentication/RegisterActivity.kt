package com.juniortech.coviddetector.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.juniortech.coviddetector.data.source.remote.RemoteDataSource.Companion.DATABASE_URL
import com.juniortech.coviddetector.data.source.remote.response.UserResponse
import com.juniortech.coviddetector.databinding.ActivityRegisterBinding
import com.juniortech.coviddetector.ui.base.MainActivity
import com.juniortech.coviddetector.ui.profile.ProfileViewModel
import com.juniortech.coviddetector.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var userDb: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getWindow().setEnterTransition(null)

        fAuth = FirebaseAuth.getInstance()
        userDb = FirebaseDatabase.getInstance(DATABASE_URL).getReference()
        binding.registerButton.setOnClickListener { registerUser() }

    }

    private fun registerUser() {
        if(!validateEmail() || !validatePassword() || !validateRetypePassword() || !validateIdCard() || !validatePhoneNumber()){
            return
        }

        val email = binding.emailRegister.editText?.text.toString().trim()
        val password = binding.passwordVerifyRegister.editText?.text.toString().trim()
        val idCard = binding.passwordVerifyRegister.editText?.text.toString().trim()
        val phoneNumber = binding.passwordVerifyRegister.editText?.text.toString().trim()

        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener( object: OnCompleteListener<AuthResult>{
            override fun onComplete(task: Task<AuthResult>) {
                if (task.isSuccessful()) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "You are logged!",
                        Toast.LENGTH_LONG
                    ).show()
                    onAuthSuccess(task.getResult()?.user, email, idCard, phoneNumber)
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Sorry, there was a mistake" + task.getException()?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        })
    }

    private fun onAuthSuccess(
        user: FirebaseUser?,
        email: String,
        idCard: String,
        phoneNumber: String
    ) {
        if (user != null) {
            val userResponse = UserResponse(userId = user.uid, userStatus = 1, userAddress = "_", userPhone = phoneNumber,
            userEmail = email, userIdCard = idCard, userName = "_")
            val factory = ViewModelFactory.getInstance(this)
            val viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

            viewModel.addNewUser(user.uid, userResponse)
            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
        }
    }

    private fun validateEmail(): Boolean{
        with(binding){
            if(emailRegister.editText?.text.toString().isEmpty()){
                emailRegister.error = "Email cannot be empty!"
                return false
            }
            emailRegister.error = null
            emailRegister.isErrorEnabled = false
            return true
        }
    }

    private fun validatePassword(): Boolean{
        with(binding){
            val password = passwordRegister.editText?.text.toString()
            if (password.isEmpty()){
                passwordRegister.error = "Password cannot be empty!"
                return false
            }else if(password.length <= 8){
                passwordRegister.error = "Password is too short!"
                return false
            }
            passwordRegister.error = null
            passwordRegister.isErrorEnabled = false
            return true
        }
    }

    private fun validateRetypePassword(): Boolean{
        with(binding){
            val password = passwordRegister.editText?.text.toString()
            val password2 = passwordVerifyRegister.editText?.text.toString()

            if (password2.isEmpty()){
                passwordVerifyRegister.error = "Password cannot be empty!"
                return false
            }else if(!password.equals(password2)){
                passwordVerifyRegister.error = "Password is not match!"
                return false
            }
            passwordVerifyRegister.error = null
            passwordVerifyRegister.isErrorEnabled = false
            return true
        }
    }

    private fun validateIdCard(): Boolean{
        with(binding){
            if(idCardRegister.editText?.text.toString().isEmpty()){
                idCardRegister.error = "Id card cannot be empty!"
                return false
            }
            idCardRegister.error = null
            idCardRegister.isErrorEnabled = false
            return true
        }
    }

    private fun validatePhoneNumber(): Boolean{
        with(binding){
            if(phoneRegister.editText?.text.toString().isEmpty()){
                phoneRegister.error = "Id card cannot be empty!"
                return false
            }
            phoneRegister.error = null
            phoneRegister.isErrorEnabled = false
            return true
        }
    }
}