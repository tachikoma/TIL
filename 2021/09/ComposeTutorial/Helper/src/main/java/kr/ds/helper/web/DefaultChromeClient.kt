package kr.ds.helper.web

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.webkit.ValueCallback
import android.webkit.WebView
import androidx.activity.ComponentActivity
import timber.log.Timber

/**
 * 이미지/동영상 등 input 태그 처리용 ChromeClient
 *
 * @param activity WebView 를 포함하는 Activity
 */
open class DefaultChromeClient(activity: Activity) : BaseChromeClient(activity) {

    private val fileChooseHelper = FileChooseHelper(targetActivity as ComponentActivity)

    /**
     * For Android Version 5.0+
     * @param webView
     * @param filePathCallback
     * @param fileChooserParams
     * @return
     */
    override fun onShowFileChooser(
        webView: WebView?, filePathCallback: ValueCallback<Array<Uri?>?>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        Timber.d(
            """onShowFileChooser A>5, OS Version : ${Build.VERSION.SDK_INT}, isCaptureEnabled:${fileChooserParams?.isCaptureEnabled}, acceptTypes:${fileChooserParams?.acceptTypes.contentToString()}"""
        )
        fileChooseHelper.runFileChooser(filePathCallback, fileChooserParams)
        return true
    }
}