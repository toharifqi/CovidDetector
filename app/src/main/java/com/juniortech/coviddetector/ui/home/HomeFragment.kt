package com.juniortech.coviddetector.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.juniortech.coviddetector.R
import com.juniortech.coviddetector.databinding.FragmentHomeBinding
import com.juniortech.coviddetector.ui.check.CheckActivity

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null){
            val sympthomAdapter = SympthomAdapter()
            val preventionsAdapter = PreventionsAdapter()
            with(binding.rvSympthom){
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
                adapter = sympthomAdapter
            }
            with(binding.rvPreventions){
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = preventionsAdapter
            }
        }




        binding.checkButton.setOnClickListener {
            startActivity(Intent(context, CheckActivity::class.java))
        }

    }
}