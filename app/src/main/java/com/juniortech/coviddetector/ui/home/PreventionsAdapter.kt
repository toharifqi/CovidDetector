package com.juniortech.coviddetector.ui.home

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.juniortech.coviddetector.R
import com.juniortech.coviddetector.data.source.local.entity.PreventionEntity
import com.juniortech.coviddetector.data.source.local.entity.SympthomEntity
import com.juniortech.coviddetector.databinding.ItemPreventionBinding
import com.juniortech.coviddetector.databinding.ItemSymptompBinding
import com.juniortech.coviddetector.utils.GenerateData

class PreventionsAdapter: RecyclerView.Adapter<PreventionsAdapter.ListViewHolder>() {
    private val listData = GenerateData.generatePreventions()

    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemPreventionBinding.bind(itemView)
        fun bind(prevention: PreventionEntity){
            with(binding){
                imgPrevention.setImageResource(prevention.image)
                txtTitle.text = prevention.title
                txtDesc.text = prevention.desc
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder =
        ListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_prevention, parent, false))

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val prevention = listData[position]
        holder.bind(prevention)
    }

    override fun getItemCount(): Int = listData.size
}