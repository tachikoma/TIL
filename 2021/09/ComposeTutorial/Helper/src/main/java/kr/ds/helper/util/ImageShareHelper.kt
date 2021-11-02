package kr.ds.helper.util

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.hardware.Camera
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ImageShareHelper(private val activity: AppCompatActivity) {

    private val permissionGrantHelper: PermissionGrantHelper = PermissionGrantHelper(activity)
    private val deleteCacheFileHelper = DeleteCacheFileHelper(activity.lifecycle)

    fun saveImageFromUrl(
        imageUrl: String,
        callback: (Uri?) -> Unit,
        deniedCallback: PermissionDeniedCallback
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissionGrantHelper.checkPermissionAndAction(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, {
                    saveImageUrl(imageUrl, callback)
                }, deniedCallback
            )
        } else {
            saveImageUrl(imageUrl, callback)
        }
    }

    fun shareImageFromUrl(imageUrl: String) {
        Glide.with(activity).asDrawable().load(imageUrl).into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
            ) {
                val uri = saveMediaToCache(
                    resource.toBitmap(
                        resource.intrinsicWidth,
                        resource.intrinsicHeight
                    )
                )
                shareFromUri(uri)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
    }

    fun saveImageFrom(
        bitmap: Bitmap,
        callback: (Uri?) -> Unit,
        deniedCallback: PermissionDeniedCallback,
        isShowCompletePopup: Boolean = false
    ) {
        val grantedCallback = {
            val uri = saveMediaToStorage(bitmap)
            if (isShowCompletePopup) {
                callback.invoke(uri)
            }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissionGrantHelper.checkPermissionAndAction(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, grantedCallback, deniedCallback
            )
        } else {
            grantedCallback.invoke()
        }
    }

    fun saveImageFrom(
        bitmap: Bitmap,
        isSaveAsJPEG: Boolean = false,
        callback: (Boolean) -> Unit,
        deniedCallback: PermissionDeniedCallback
    ) {
        val grantedCallback = {
            val uri = saveMediaToStorage(bitmap, isSaveAsJPEG)
            uri?.let {
                callback.invoke(true)
            } ?: run {
                callback.invoke(false)
            }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissionGrantHelper.checkPermissionAndAction(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, grantedCallback, deniedCallback
            )
        } else {
            grantedCallback.invoke()
        }
    }

    fun cacheImageFrom(bitmap: Bitmap, callback: (Uri?) -> Unit) {
        Glide.with(activity).asDrawable().load(bitmap).into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
            ) {
                val uri = saveMediaToCache(
                    resource.toBitmap(
                        resource.intrinsicWidth,
                        resource.intrinsicHeight
                    )
                )
                callback.invoke(uri)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
    }

    fun shareImageFrom(bitmap: Bitmap, isSaveAsJPEG: Boolean = false) {
        Glide.with(activity).asDrawable().load(bitmap).into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
            ) {
                val uri = saveMediaToCache(
                    resource.toBitmap(
                        resource.intrinsicWidth,
                        resource.intrinsicHeight
                    ), isSaveAsJPEG
                )
                shareFromUri(uri)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
    }

    private fun shareFromUri(uri: Uri?) {
        uri?.let {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(
                    Intent.EXTRA_STREAM,
                    uri
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = "image/*"
            }
            val resInfoList = activity.packageManager.queryIntentActivities(
                shareIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                activity.grantUriPermission(
                    packageName,
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            activity.startActivity(Intent.createChooser(shareIntent, null))
        } ?: run {
            Timber.d("이미지 캐싱 실패")
        }
    }

    private fun saveImageUrl(imageUrl: String, callback: (Uri?) -> Unit) {
        Glide.with(activity).asDrawable().load(imageUrl).into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
            ) {
                val uri = saveMediaToStorage(
                    resource.toBitmap(
                        resource.intrinsicWidth,
                        resource.intrinsicHeight
                    )
                )
                callback.invoke(uri)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
    }

    private fun saveMediaToStorage(bitmap: Bitmap, isSaveAsJPEG: Boolean = false): Uri? {
        var uri: Uri? = null
        val ext = if (isSaveAsJPEG) "jpg" else "png"
        val filename = "${System.currentTimeMillis()}.$ext"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/$ext")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                Timber.d("imageUri = $imageUri")
                uri = imageUri
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            val imageUri = FileProvider.getUriForFile(
                activity,
                "${activity.packageName}.fileprovider",
                image
            )
            Timber.d("imageUri = $imageUri")
            uri = imageUri
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(
                if (isSaveAsJPEG) Bitmap.CompressFormat.JPEG else Bitmap.CompressFormat.PNG,
                100,
                it
            )
            Timber.d("Saved to $ext")

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                activity.sendBroadcast(
                    Intent(Camera.ACTION_NEW_PICTURE, uri)
                )
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val savedFile = File(imagesDir, filename)
                val mimeType = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(savedFile.extension)
                MediaScannerConnection.scanFile(
                    activity,
                    arrayOf(savedFile.absolutePath),
                    arrayOf(mimeType)
                ) { _, uri ->
                    Timber.d("Image capture scanned into media store: $uri")
                }
            }
        }

        return uri
    }

    private fun saveMediaToCache(bitmap: Bitmap, isSaveAsJPEG: Boolean = false): Uri? {
        val ext = if (isSaveAsJPEG) "jpg" else "png"
        val filename = "${System.currentTimeMillis()}.$ext"
        val imagesDir = activity.cacheDir
        val imageFile = File(imagesDir, filename)
        val imageUri = FileProvider.getUriForFile(
            activity,
            "${activity.packageName}.fileprovider",
            imageFile
        )
        Timber.d("imageUri = $imageUri")
        val uri: Uri? = imageUri
        val fos = FileOutputStream(imageFile)
        fos.use {
            bitmap.compress(
                if (isSaveAsJPEG) Bitmap.CompressFormat.JPEG else Bitmap.CompressFormat.PNG,
                100,
                it
            )
            Timber.d("Cached to $ext")
            deleteCacheFileHelper.add(imageFile)
        }

        return uri
    }
}
