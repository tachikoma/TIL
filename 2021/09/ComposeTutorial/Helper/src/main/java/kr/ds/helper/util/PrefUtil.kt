package kr.ds.helper.util

import android.content.Context
import android.content.SharedPreferences

object PrefUtil {
    fun init(context: Context, prefName: String): SharedPreferences
            = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

    fun putBoolean(pref: SharedPreferences, k: String, v: Boolean?) = pref.run {
        edit().putBoolean(k, v ?: false).apply()
    }

    fun putInt(pref: SharedPreferences, k: String, v: Int?) = pref.run {
        edit().putInt(k, (v ?: 0)).apply()
    }

    fun putLong(pref: SharedPreferences, k: String, v: Long?) = pref.run {
        edit().putLong(k, (v ?: 0)).apply()
    }

    fun putFloat(pref: SharedPreferences, k: String, v: Float?) = pref.run {
        edit().putFloat(k, (v ?: 0f)).apply()
    }

    fun putString(pref: SharedPreferences, k: String, v: String?) = pref.run {
        edit().putString(k, (v ?: "")).apply()
    }

    fun getBoolean(pref: SharedPreferences, k: String, defaultValue: Boolean = false): Boolean = pref.run {
        getBoolean(k, defaultValue)
    }

    fun getInt(pref: SharedPreferences, k: String): Int = pref.run {
        getInt(k, 0)
    }

    fun getLong(pref: SharedPreferences, k: String): Long = pref.run {
        getLong(k, 0)
    }

    fun getFloat(pref: SharedPreferences, k: String): Float = pref.run {
        getFloat(k, 0f)
    }

    fun getString(pref: SharedPreferences, k: String, defaultValue: String = ""): String = pref.run {
        getString(k, defaultValue)
    } ?: defaultValue

    fun clear(pref: SharedPreferences) = pref.run {
        edit().clear().apply()
    }

}