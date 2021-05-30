package com.juniortech.coviddetector.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.juniortech.coviddetector.R
import com.juniortech.coviddetector.data.source.local.entity.UserEntity
import com.juniortech.coviddetector.databinding.FragmentProfileBinding
import com.juniortech.coviddetector.ui.authentication.LoginActivity
import com.juniortech.coviddetector.viewmodel.ViewModelFactory
import com.juniortech.coviddetector.vo.Status

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory.getInstance(requireActivity())
        val viewModel = ViewModelProvider(requireActivity(), factory)[ProfileViewModel::class.java]
        val fAuth = FirebaseAuth.getInstance()

        fAuth.currentUser?.let {
            viewModel.user(it.uid).observe(viewLifecycleOwner, { userEntity ->
                if (userEntity != null){
                    when(userEntity.status){
                        Status.LOADING -> binding.progressBar.visibility = View.VISIBLE
                        Status.SUCCESS -> if (userEntity.data != null){
                            binding.progressBar.visibility = View.GONE
                            populateView(userEntity.data)

                        }
                        Status.ERROR -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(context, "There was a mistake", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }

    }

    private fun populateView(data: UserEntity) {
        binding.userName.text = data.userName
        binding.userEmail.text = data.userEmail
        binding.userIdcard.text = data.userIdCard
        binding.userPhone.text = data.userPhone
        binding.userAddress.text = data.userAddress
        Glide.with(requireActivity())
            .load(data.userPhoto)
            .apply(RequestOptions.placeholderOf(R.drawable.ic_loading))
            .error(R.drawable.user_placeholder)
            .into(binding.userImage)
    }
}