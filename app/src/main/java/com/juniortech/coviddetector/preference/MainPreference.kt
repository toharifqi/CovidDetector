package com.juniortech.coviddetector.preference

import android.content.Context
import androidx.core.content.edit

class MainPreference(context: Context) {
    companion object{
        private const val PREFS_NAME = "main_pref"
        private const val NOT_FIRST_INSTALL = "not_first_install"
    }

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setIfNotFirstInstall(boolean: Boolean){
        preferences.edit {
            putBoolean(NOT_FIRST_INSTALL, boolean)
        }
    }

    fun isNotFirstInstall(): Boolean{
        return preferences.getBoolean(NOT_FIRST_INSTALL, false)
    }
}