package com.example.composetutorial

import android.app.Activity
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import com.example.composetutorial.webbridge.ImageHandler
import com.example.composetutorial.webbridge.ShowHandler
import kr.ds.helper.extension.readStringFromAsset
import kr.ds.helper.util.ImageShareHelper
import kr.ds.helper.web.*
import timber.log.Timber

class WebViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent.getStringExtra("EXTRA_URL")

        addWebBridgeHandlers()

        val onBack: (WebView?) -> Unit = { webView ->
            if (true == webView?.canGoBack()) {
                webView.goBack()
            } else {
                finish()
            }
        }

        setContent {
            ComposeTutorialTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    url?.let {
                        RootScreenUrl(
                            it,
                            defaultWebViewClient,
                            defaultChromeClient,
                            webBridge, onBack
                        )
                    } ?: run {
                        RootScreenData(
                            readStringFromAsset("test.html"),
                            defaultWebViewClient,
                            defaultChromeClient,
                            webBridge, onBack
                        )
                    }
                }
            }
        }
    }

    private val imageShareHelper = ImageShareHelper(this)

    private fun addWebBridgeHandlers() {
        webBridge.also {
            it.addInterface("image", object : ImageHandler {
                override fun save(args: WebMessageArgs?) {
                    val imgUrl = args?.get("imgUrl") as String
                    Timber.d("url = $imgUrl")
                    imageShareHelper.saveImageFromUrl(imgUrl, {

                    }, {
                    })
                }

                override fun share(args: WebMessageArgs?) {
                    val imgUrl = args?.get("imgUrl") as String
                    Timber.d("url = $imgUrl")
                    imageShareHelper.shareImageFromUrl(imgUrl)
                }
            })
            it.addInterface("show", object : ShowHandler {
                override fun camera(args: WebMessageArgs?) {
                    Timber.d("args= $args")
                }
            })
        }
    }

    private val defaultChromeClient = OpenWindowChromeClient(this)

    private val defaultWebViewClient =
        BaseWebViewClient(this, object : IDefaultWebViewClientStrategy {
            override val webViewActivity: Activity
                get() = this@WebViewActivity

            override fun handleHttpUrl(url: String): Boolean {
                return false
            }

            override fun isDefineError(errorCode: Int): Boolean {
                return webViewErrorHelper.isDefinedError(errorCode)
            }

            override fun showError(view: WebView?, errMsg: String) {
            }

            private val webViewErrorHelper = DefaultWebViewErrorHelper()
        })

    private val webBridge = DefaultWebBridge()
}

@Composable
fun RootScreenUrl(
    url: String,
    defaultWebViewClient: BaseWebViewClient? = null,
    defaultWebChromeClient: DefaultChromeClient? = null,
    webBridge: WebBridge? = null,
    onBack: ((WebView?) -> Unit)? = null,
) {
    WebViewScreen(modifier = Modifier.fillMaxHeight(), onInit = {
        it.loadUrl(url)
    }, defaultWebViewClient, defaultWebChromeClient, webBridge, onBack = onBack)
}

@Composable
fun RootScreenData(
    data: String,
    defaultWebViewClient: BaseWebViewClient? = null,
    defaultWebChromeClient: DefaultChromeClient? = null,
    webBridge: WebBridge? = null,
    onBack: ((WebView?) -> Unit)? = null,
) {
    WebViewScreen(modifier = Modifier.fillMaxHeight(), onInit = {
        it.settings.setSupportMultipleWindows(true)
        it.loadData(data, "text/html", "UTF-8")
    }, defaultWebViewClient, defaultWebChromeClient, webBridge, onBack = onBack)
}


@Preview(showBackground = true)
@Composable
fun DefaultPreviewRootScreen() {
    ComposeTutorialTheme {
        RootScreenUrl("https://m.daum.net")
    }
}