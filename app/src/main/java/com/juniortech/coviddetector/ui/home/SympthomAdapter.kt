package com.juniortech.coviddetector.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.juniortech.coviddetector.R
import com.juniortech.coviddetector.data.source.local.entity.SympthomEntity
import com.juniortech.coviddetector.databinding.ItemSymptompBinding
import com.juniortech.coviddetector.utils.GenerateData

class SympthomAdapter: RecyclerView.Adapter<SympthomAdapter.ListViewHolder>(){
    private val listData = GenerateData.generateSympthoms()

    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemSymptompBinding.bind(itemView)
        fun bind(sympthom: SympthomEntity){
            with(binding){
                imgSympt.setImageResource(sympthom.image)
                txtSympt.text = sympthom.text
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder =
        ListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_symptomp, parent, false))

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val sympthom = listData[position]
        holder.bind(sympthom)
    }

    override fun getItemCount(): Int = listData.size
}