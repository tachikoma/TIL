package kr.ds.helper.extension

import android.app.ActivityManager
import android.app.Service
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.view.ViewConfiguration
import android.view.WindowManager
import androidx.annotation.AnyRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStream
import kotlin.system.exitProcess

/**
 * Returns true if this is a foreground service.
 *
 * @param className The Service Class Name [String].
 */
fun Context.serviceIsRunningInForeground(className: String): Boolean {
    val manager = getSystemService(
        Service.ACTIVITY_SERVICE
    ) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (className == service.service.className) {
            if (service.foreground) {
                return true
            }
        }
    }
    return false
}

val Context.isNetworkAvailable: Boolean
    get() {
        try {
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            Timber.d(connectivityManager.activeNetworkInfo?.typeName)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    when {
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            return true
                        }
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            return true
                        }
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                            return true
                        }
                    }
                }
            } else {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                    return true
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
//            FirebaseCrashlytics.getInstance().recordException(e)
        }
        return false
    }

val Context.isWifiEnabled: Boolean
    get() {
        val wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        return wifi?.isWifiEnabled ?: false
    }

fun Context.pxFrom(value: Float): Int = (value * resources.displayMetrics.density).toInt()

fun Context.readStringFromAsset(fileName: String): String {
    return assets.open(fileName).bufferedReader()
        .use(BufferedReader::readText)
}

fun Context.inputStreamFromAsset(fileName: String): InputStream {
    return assets.open(fileName)
}

fun Context.resourceUri(@AnyRes resId: Int): Uri {
    return resources.let {
        Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(it.getResourcePackageName(resId))		// it : resources, this : ResId(Int)
            .appendPath(it.getResourceTypeName(resId))		// it : resources, this : ResId(Int)
            .appendPath(it.getResourceEntryName(resId))		// it : resources, this : ResId(Int)
            .build()
    }
}

/**
 * App version
 *
 * @return
 */
val Context.appVersion: String
    get() {
        var versionName = ""
        try {
            val info = packageManager.getPackageInfo(packageName, 0)
            versionName = info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return versionName
    }

/**
 *
 * @return status bar + ? + navigation bar
 */
fun Context.getExtraSize(): Point {
    val appUsableSize: Point = getAppUsableScreenSize()
    val realScreenSize: Point = getRealScreenSize()

    // extra on the side
    if (appUsableSize.x < realScreenSize.x) {
        return Point(realScreenSize.x - appUsableSize.x, appUsableSize.y)
    }

    // extra at the bottom
    return if (appUsableSize.y < realScreenSize.y) {
        Point(appUsableSize.x, realScreenSize.y - appUsableSize.y)
    } else Point()

    // extra is not present
}

fun Context.getAppUsableScreenSize(): Point {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size
}

fun Context.getRealScreenSize(): Point {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val size = Point()
    display.getRealSize(size)
    return size
}

fun Context.heightForStatusBar(): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    Timber.d("status_bar_height:${result}")
    return result
}

fun Context.heightForNavigationBar(): Int {
    val hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey()
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    val r = if (resourceId > 0 && !hasMenuKey) {
        resources.getDimensionPixelSize(resourceId)
    } else 0
    val y = getExtraSize().y
    Timber.d(
        "navigationBar: ${r}, getExtraSize:$y"
    )
    return r
}

fun Context.metadataFromManifest(key: String): String? {
    var value: String? = null
    try {
        val ai = packageManager
            .getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val bundle = ai.metaData
        if (bundle != null) {
            value = bundle.getString(key)
        }
    } catch (e: Exception) {
        Timber.e(
            "Caught non-fatal exception while retrieving $key: $e"
        )
    }
    return value
}

fun Context.isPackageInstalled(pkgName: String): Boolean {
    try {
        packageManager.getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        return false
    }
    return true
}

/**
 * 현재 다크 모드 인지 여부
 */
fun Context.isUsingNightModeResources(): Boolean {
    return when (resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> true
        Configuration.UI_MODE_NIGHT_NO -> false
        Configuration.UI_MODE_NIGHT_UNDEFINED -> false
        else -> false
    }
}

fun Context.showPermissionRequestDialog(
    title: String,
    body: String,
    callback: () -> Unit
) {
    AlertDialog.Builder(this).also {
        it.setTitle(title)
        it.setMessage(body)
        it.setPositiveButton(android.R.string.ok) { _, _ ->
            callback()
        }
    }.create().show()
}

/**
 * 앱 종료
 */
fun Context.appFinish() {
    val activity = when (this) {
        is AppCompatActivity -> this
        is ContextWrapper -> {
            val contextWrapper = this
            if (contextWrapper.baseContext is AppCompatActivity) {
                contextWrapper.baseContext as AppCompatActivity
            } else {
                null
            }
        }
        else -> {
            null
        }
    }
    activity?.let {
        ActivityCompat.finishAffinity(it)      // 해당 앱의 루트 액티비티를 종료시킨다.
        it.moveTaskToBack(true)      // 태스크를 뒤로 보냄.
    } ?: run {
//        debugToast { "AppCompatActivity 가 아니어서 종료할 수 없음" }
    }
    System.runFinalization()       // 현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어
    exitProcess(0)         // 현재 액티비티를 종료 시킨다.
}

fun Context.activityFinish() {
    val activity = when (this) {
        is AppCompatActivity -> this
        is ContextWrapper -> {
            val contextWrapper = this
            if (contextWrapper.baseContext is AppCompatActivity) {
                contextWrapper.baseContext as AppCompatActivity
            } else {
                null
            }
        }
        else -> {
            null
        }
    }

    activity?.finish()
}