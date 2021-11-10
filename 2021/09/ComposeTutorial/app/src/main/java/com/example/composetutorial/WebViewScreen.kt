package com.example.composetutorial

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch
import kr.ds.helper.web.*

@Composable
fun WebViewScreen(
    modifier: Modifier = Modifier,
    onInit: (WebView) -> Unit,
    webViewClientImpl: BaseWebViewClient? = null,
    chromeClientImpl: DefaultChromeClient? = null,
    webBridge: WebBridge? = null,
    onBack: ((WebView?) -> Unit)? = null,
) {
    var webView: WebView? = null
    val coroutineScope = rememberCoroutineScope()
    AndroidView({ context ->
        WebView(context).apply {
            initWebView()
            chromeClientImpl?.let {
                webChromeClient = it
            }
            webViewClientImpl?.let {
                webViewClient = it
            }
            webBridge?.let {
                addWebViewBridge(it)
            }
            webView = this
            onInit(this)
        }
    }, modifier = modifier)
    BackHandler {
        coroutineScope.launch {
            onBack?.invoke(webView)
        }
    }
}

@Composable
fun WebPageScreen(modifier: Modifier = Modifier, urlToRender: String) {
    AndroidView(factory = ::WebView, modifier = modifier) { webView ->
        with(webView) {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            loadUrl(urlToRender)
        }
    }
}