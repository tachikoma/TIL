package kr.ds.helper.web

import android.webkit.JavascriptInterface
import androidx.annotation.Keep
import androidx.annotation.MainThread
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.URLDecoder

abstract class WebBridge {

    companion object {
        const val DEFAULT_WEB_BRIDGE_NAME = "androidWebBridge"
    }

    private val gson by lazy {
        Gson()
    }

    @Keep
    @JavascriptInterface
    fun postMessage(message: String) {
        Timber.d(message)
        val decoded = URLDecoder.decode(message, "utf-8")
        val webMessage = gson.fromJson(decoded, WebMessage::class.java)
        if (webMessage.group.isBlank())
            return

        runFunction(webMessage)
    }

    @MainThread
    fun runFunction(webMessage: WebMessage) {
        Timber.d("${webMessage.group}.${webMessage.function}(${webMessage.args})")
        // 여기가 호출되면 메인 쓰레드가 아님, 그래서 메인 쓰레드에서 동작이 필요한 경우는 아래와 같이 처리 되어야 함
        CoroutineScope(Dispatchers.Main).launch {
            onWebMessage(webMessage)
        }
    }

    abstract fun onWebMessage(webMessage: WebMessage)
}
