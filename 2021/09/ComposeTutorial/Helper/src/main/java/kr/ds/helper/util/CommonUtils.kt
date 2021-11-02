package kr.ds.helper.util

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import timber.log.Timber
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

fun getScreenWidthToPx(): Int = Resources.getSystem().displayMetrics.widthPixels
fun getScreenHeightToPx(): Int = Resources.getSystem().displayMetrics.heightPixels

inline val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

inline val Float.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

inline val Float.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Bundle?.toKVString(): String {
    var msg = ""
    this?.keySet()?.let {
        for (key in it) {
            val value = this.get(key)
            msg += if (value is Bundle) {
                "$key : [${value.toKVString()}]\n"
            } else
                "$key : $value\n"
        }
    } ?: run {
        return "null"
    }
    return msg
}

fun Context.printSignKeySHA() {
    try {
        val info = packageManager.getPackageInfo(
            packageName,
            PackageManager.GET_SIGNATURES
        )
        for (signature in info.signatures) {
            val md: MessageDigest = MessageDigest.getInstance("SHA")
            md.update(signature.toByteArray())
            val keyHash = md.digest().toHex(":")
            println("KeyHash: $keyHash")
            Timber.d("KeyHash: $keyHash")
        }
    } catch (e: PackageManager.NameNotFoundException) {
        Timber.e(e)
    } catch (e: NoSuchAlgorithmException) {
        Timber.e(e)
    }
}

fun ByteArray.toHex(separator: String = ""): String =
    joinToString(separator = separator) { eachByte -> "%02x".format(eachByte) }