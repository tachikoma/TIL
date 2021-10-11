package kr.ds.helper.web

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.webkit.WebSettings

/**
 * API or WebView UserAgent Manager
 */
object UserAgentManager {
    private var mUserAgent: String = ""

    private lateinit var APP_ID: String
    private lateinit var APP_VERSION: String

    private val userAgentExtra: String
        get() {
            return ("$SECTION_DELIMITER$APP_ID"
                    + "$DELIMITER$APP_VERSION$DELIMITER${Build.MODEL}")
        }

    val userAgent: String
        get() = if (TextUtils.isEmpty(mUserAgent)) {
            val newUserAgent = defaultUserAgent + userAgentExtra
            mUserAgent = newUserAgent
            newUserAgent
        } else {
            mUserAgent
        }

    private const val DELIMITER = "/"
    private const val SECTION_DELIMITER = " "
    private const val CUSTOM_USER_AGENT =
        "Mozilla/5.0 (Linux; Android 8.0.0; Pixel 2 XL Build/OPD1.170816.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.183 Mobile Safari/537.36"

    private var defaultUserAgent: String = CUSTOM_USER_AGENT

    fun genUserAgent(context: Context, appId: String, appVersion: String) {
        try {
            APP_ID = appId
            APP_VERSION = appVersion
            defaultUserAgent = WebSettings.getDefaultUserAgent(context)
        } catch (e: Exception) {
//            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}