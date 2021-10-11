package com.example.composetutorial

import android.app.Activity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import kotlinx.coroutines.launch
import kr.ds.helper.extension.appVersion
import kr.ds.helper.web.*

class WebViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent.getStringExtra("EXTRA_URL")

        UserAgentManager.genUserAgent(this, packageName.substringAfter('.'), appVersion)

        setContent {
            ComposeTutorialTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    url?.let {
                        RootScreen(it, defaultWebViewClient, defaultChromeClient) { webView ->
                            if (true == webView?.canGoBack()) {
                                webView.goBack()
                            } else {
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }

    private val defaultChromeClient = DefaultChromeClient(this)

    private val defaultWebViewClient =
        BaseWebViewClient(this, object : IDefaultWebViewClientStrategy {
            override val webViewActivity: Activity
                get() = this@WebViewActivity

            override fun handleHttpUrl(url: String): Boolean {
                return false
            }

            override fun isDefineError(errorCode: Int): Boolean {
                return false
            }

            override fun showError(view: WebView?, errMsg: String) {
            }

        })
}

@Composable
fun RootScreen(
    url: String,
    defaultWebViewClient: BaseWebViewClient? = null,
    defaultWebChromeClient: DefaultChromeClient? = null,
    onBack: ((WebView?) -> Unit)? = null,
) {
    var webView: WebView? = null
    val coroutineScope = rememberCoroutineScope()
    AndroidView({ context ->
        WebView(context).apply {
            loadUrl(url)
            initWebView()
            defaultWebChromeClient?.let {
                webChromeClient = it
            }
            defaultWebViewClient?.let {
                webViewClient = it
            }
            webView = this
        }
    })
    BackHandler {
        coroutineScope.launch {
            onBack?.invoke(webView)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewRootScreen() {
    ComposeTutorialTheme {
        RootScreen("https://m.daum.net")
    }
}