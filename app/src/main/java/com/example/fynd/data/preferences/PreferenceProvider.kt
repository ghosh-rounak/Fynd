package com.example.fynd.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

//This class is not used any where but kept here for future probable use of Shared Preference
class PreferenceProvider(
    context: Context
) {

    private val appContext = context.applicationContext

    private val preference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)


    fun saveInSharedPref(itemValue: String,key:String) {
        preference.edit().putString(
            key,
            itemValue
        ).apply()
    }

    fun getFromSharedPref(key:String): String? {
        return preference.getString(key, null)
    }

}