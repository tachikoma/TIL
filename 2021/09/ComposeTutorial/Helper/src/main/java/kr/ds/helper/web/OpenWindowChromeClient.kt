package kr.ds.helper.web

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Message
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import timber.log.Timber
import android.webkit.WebResourceRequest

import android.webkit.WebViewClient


/**
 * Multiple Window 지원 ChromeClient
 *
 * 사용하려면 해당 WebView 에 대해 settings.setSupportMultipleWindows(true) 호출이 필요함
 */
class OpenWindowChromeClient(activity: Activity) : DefaultChromeClient(activity) {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?
    ): Boolean {
        Timber.d("isDialog:$isDialog, isUserGesture:$isUserGesture, resultMsg:$resultMsg")
        val newWebView = WebView(targetActivity)
        newWebView.initWebView()
        var dialog: Dialog? = null
        if (isDialog) {
            dialog = Dialog(targetActivity).apply {
                setContentView(newWebView)
            }
            dialog.show()
            val lp = WindowManager.LayoutParams().apply {
                copyFrom(dialog.window!!.attributes)
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.MATCH_PARENT
            }
            dialog.window?.attributes = lp
        } else {
            view?.addView(newWebView)
        }
        newWebView.webChromeClient = object : WebChromeClient() {
            override fun onCloseWindow(window: WebView) {
                Timber.d("")
                dialog?.dismiss()
            }
        }
        newWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                return false
            }
        }
        (resultMsg?.obj as WebView.WebViewTransport).webView = newWebView
        resultMsg.sendToTarget()
        return true
    }

    override fun onCloseWindow(window: WebView?) {
        super.onCloseWindow(window)
        Timber.d("")
    }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        super.onShowCustomView(view, callback)
        Timber.d("$view")
    }

    override fun onHideCustomView() {
        super.onHideCustomView()
        Timber.d("")
    }
}