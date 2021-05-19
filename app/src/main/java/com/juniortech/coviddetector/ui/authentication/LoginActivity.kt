package com.juniortech.coviddetector.ui.authentication

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Pair
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.juniortech.coviddetector.R
import com.juniortech.coviddetector.data.source.remote.RemoteDataSource.Companion.DATABASE_URL
import com.juniortech.coviddetector.databinding.ActivityLoginBinding
import com.juniortech.coviddetector.ui.base.MainActivity
import com.juniortech.coviddetector.ui.profile.ProfileViewModel
import com.juniortech.coviddetector.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fAuth = FirebaseAuth.getInstance()
        if (fAuth.currentUser != null){
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }

        binding.registerButton.setOnClickListener(this)
        binding.loginButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id){
                R.id.register_button -> {
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)

                    val emailPair = Pair.create<View, String>(binding.emailLogin, "email_animate")
                    val passwordPair = Pair.create<View, String>(binding.passwordLogin, "password_animate")
                    val loginPair = Pair.create<View, String>(binding.loginButton, "button_animate")

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        val options = ActivityOptions.makeSceneTransitionAnimation(this@LoginActivity, emailPair, passwordPair, loginPair)
                        getWindow().setExitTransition(null)
                        startActivity(intent, options.toBundle())
                    }else{
                        startActivity(intent)
                    }
                }
                R.id.login_button -> {
                    loginUser()
                }
            }
        }
    }

    private fun loginUser() {
        if (!validateEmail() || !validatePassword()){
            return
        }

        val email = binding.emailLogin.editText?.text.toString().trim()
        val password = binding.passwordLogin.editText?.text.toString().trim()

        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, object : OnCompleteListener<AuthResult>{
            override fun onComplete(task: Task<AuthResult>) {
                if (task.isSuccessful){
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(
                        this@LoginActivity,
                        "Cannot login, make sure the email and password are correct!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
    }

    private fun validateEmail(): Boolean{
        with(binding){
            if(emailLogin.editText?.text.toString().isEmpty()){
                emailLogin.error = "Email cannot be empty!"
                return false
            }
            emailLogin.error = null
            emailLogin.isErrorEnabled = false
            return true
        }
    }

    private fun validatePassword(): Boolean{
        with(binding){
            val password = passwordLogin.editText?.text.toString()
            if (password.isEmpty()){
                passwordLogin.error = "Password cannot be empty!"
                return false
            }else if(password.length <= 8){
                passwordLogin.error = "Password is too short!"
                return false
            }
            passwordLogin.error = null
            passwordLogin.isErrorEnabled = false
            return true
        }
    }
}