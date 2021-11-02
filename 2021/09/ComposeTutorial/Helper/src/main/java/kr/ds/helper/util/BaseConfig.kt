package kr.ds.helper.util

import android.content.Context
import android.content.SharedPreferences
import com.vinylc.runable.util.PrefUtil

/**
 * SharedPreferences Read & Write Util
 */

abstract class BaseConfig(context: Context) {
    protected val configPref: SharedPreferences by lazy { PrefUtil.init(context, CONFIG_PREF_NAME) }

    companion object {
        const val CONFIG_PREF_NAME: String = "config_pref"
    }
}