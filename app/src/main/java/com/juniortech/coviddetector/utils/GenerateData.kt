package com.juniortech.coviddetector.utils

import com.juniortech.coviddetector.R
import com.juniortech.coviddetector.data.source.local.entity.PreventionEntity
import com.juniortech.coviddetector.data.source.local.entity.SympthomEntity

object GenerateData {
    fun generateSympthoms(): List<SympthomEntity>{
        val sympthoms = ArrayList<SympthomEntity>()

        sympthoms.add(
            SympthomEntity(R.drawable.cough, "Cough")
        )
        sympthoms.add(
            SympthomEntity(R.drawable.fever, "Fever")
        )
        sympthoms.add(
            SympthomEntity(R.drawable.headache, "Headache")
        )
        sympthoms.add(
            SympthomEntity(R.drawable.fever, "Tiredness")
        )

        return sympthoms
    }

    fun generatePreventions(): List<PreventionEntity>{
        val preventions = ArrayList<PreventionEntity>()

        preventions.add(
            PreventionEntity(R.drawable.wear_mask, "Wear Face Mask", "Since the start of the coronavirus outbreak same place have fully embraced wearing face masks, and anyone cought without one risks becoming a social…")
        )
        preventions.add(
            PreventionEntity(R.drawable.wash_hands, "Wash Your Hands", "These diseases include gastrointestinal infections. Such as Salmonella, and respiratory infections, such as influenza…")
        )

        return preventions
    }
}