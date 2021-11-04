package kr.ds.helper.web

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.webkit.ValueCallback
import android.webkit.WebChromeClient.FileChooserParams
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import timber.log.Timber
import java.io.File

class FileChooseHelper(private val activity: ComponentActivity) {
    private var cameraImageUri: Uri? = null
    private var cameraVideoUri: Uri? = null

    private var filePathCallbackLollipop: ValueCallback<Array<Uri?>?>? = null

    val isOnWorking: Boolean
        get() = null != filePathCallbackLollipop

    private val requestActivityLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        Timber.d("lollipop or more. data ${it.data}")
        if (Activity.RESULT_OK == it.resultCode) {
            if (null != filePathCallbackLollipop) {
                val resultUri = getResultUri(it.data)
                val results: Array<Uri?>? = resultUri?.let { arrayOf(resultUri) }
                filePathCallbackLollipop!!.onReceiveValue(results)
                filePathCallbackLollipop = null
            }
        } else {
            if (null != filePathCallbackLollipop) {
                filePathCallbackLollipop!!.onReceiveValue(null)
                filePathCallbackLollipop = null
            }
        }
    }


    /**
     * 이미지 경로를 찾는 로직
     *
     * @param data
     * @return
     */
    private fun getResultUri(data: Intent?): Uri? {
        var result: Uri? = null
        if (data == null || TextUtils.isEmpty(data.dataString)) {
            // If there is not data, then we may have taken a photo
            if (null != cameraImageUri) {
                result = cameraImageUri
            }
            if (null != cameraVideoUri) {
                result = cameraVideoUri
            }
        } else {
            val filePath: String? = data.dataString
            result = Uri.parse(filePath)
        }
        return result
    }

    private fun setFilePathCallbackLollipop(filePathCallbackLollipop: ValueCallback<Array<Uri?>?>?) {
        if (null != this.filePathCallbackLollipop) {
            this.filePathCallbackLollipop!!.onReceiveValue(null)
            this.filePathCallbackLollipop = null
        }
        this.filePathCallbackLollipop = filePathCallbackLollipop!!
    }

    fun runFileChooser(
        filePathCallbackLollipop: ValueCallback<Array<Uri?>?>?,
        fileChooserParams: FileChooserParams?
    ) {
        Timber.d(
            """acceptTypes:${fileChooserParams?.acceptTypes.contentToString()}, isCaptureEnabled:${fileChooserParams?.isCaptureEnabled}, mode:${fileChooserParams?.mode}, title:${fileChooserParams?.title}, fileHint:${fileChooserParams?.filenameHint} """
        )
        setFilePathCallbackLollipop(filePathCallbackLollipop)
        runChooser(fileChooserParams)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun runChooser(fileChooserParams: FileChooserParams?) {
        try {
            if (!fileChooserParams!!.isCaptureEnabled) {
                val pickIntent = chooserIntent(fileChooserParams)
                val chooserIntent = Intent.createChooser(pickIntent, null)
                if (shouldUseCaptureForEmptyTypes && 0 == fileChooserParams.acceptTypes.count { type -> type.isNotBlank() }) {
                    chooserIntent.putExtra(
                        Intent.EXTRA_INITIAL_INTENTS,
                        arrayOf(
                            imageCaptureIntent(),
                            videoCaptureIntent(),
                            audioCaptureIntent()
                        ).filterNotNull().toTypedArray()
                    )
                }
                if (shouldUseCaptureForAllTypes && 1 == fileChooserParams.acceptTypes.count { type -> type == "*/*" }) {
                    chooserIntent.putExtra(
                        Intent.EXTRA_INITIAL_INTENTS,
                        arrayOf(
                            imageCaptureIntent(),
                            videoCaptureIntent(),
                            audioCaptureIntent()
                        ).filterNotNull().toTypedArray()
                    )
                }
                requestActivityLauncher.launch(chooserIntent)
                return
            }

            val pickIntent = chooserIntent(fileChooserParams)
            val chooserIntent = Intent.createChooser(pickIntent, null)

            chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                arrayOfCaptureIntents(fileChooserParams)
            )

            requestActivityLauncher.launch(chooserIntent)
        } catch (e: SecurityException) {
            if (null != filePathCallbackLollipop) {
                filePathCallbackLollipop!!.onReceiveValue(null)
                filePathCallbackLollipop = null
            }
        }
    }

    private fun arrayOfCaptureIntents(fileChooserParams: FileChooserParams): Array<Intent> {
        var intentArray: Array<Intent?> = arrayOfNulls(0)

        if (0 == fileChooserParams.acceptTypes.count { types -> types.isNotBlank() }) {
            intentArray = intentArray.plus(imageCaptureIntent())
            intentArray = intentArray.plus(videoCaptureIntent())
            intentArray = intentArray.plus(audioCaptureIntent())
        } else {
            // 이미지 캡쳐 intent 포함시키기..
            if (fileChooserParams.acceptTypes.count { it.contains("image/", true) } > 0) {
                intentArray = intentArray.plus(imageCaptureIntent())
            }

            // 비디오 캡쳐 intent 포함시키기..
            if (fileChooserParams.acceptTypes.count { it.contains("video/", true) } > 0) {
                intentArray = intentArray.plus(videoCaptureIntent())
            }

            // 오디오 캡쳐 intent 포함시키기..
            if (fileChooserParams.acceptTypes.count { it.contains("audio/", true) } > 0) {
                intentArray = intentArray.plus(audioCaptureIntent())
            }
        }
        return intentArray.filterNotNull().toTypedArray()
    }

    private fun chooserIntent(fileChooserParams: FileChooserParams): Intent {
        val chooserIntent = fileChooserParams.createIntent()
        val actualTypes = fileChooserParams.acceptTypes.filter { it.isNotBlank() }
        if (actualTypes.isNotEmpty()) {
            chooserIntent.putExtra(Intent.EXTRA_MIME_TYPES, actualTypes.toTypedArray())
        }
        return chooserIntent
    }

    private fun imageCaptureIntent(): Intent? {
        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intentCamera.resolveActivity(activity.packageManager) != null) {
            val path = activity.cacheDir
            val tempPath = File(path, "/temp_pic")
            if (!tempPath.exists()) {
                if (!tempPath.mkdirs()) {
                    Timber.e("mkdirs fail ${tempPath.absolutePath}")
                }
            } else {
                tempPath.listFiles { pathname ->
                    pathname?.isFile == true && pathname.absolutePath.contains(
                        Companion.PREFIX,
                        true
                    )
                }?.forEach { file ->
                    if (!file.delete()) {
                        Timber.e("delete fail ${file.absolutePath}")
                    }
                }
            }
            val file = File(tempPath, Companion.PREFIX + System.currentTimeMillis() + ".jpg")
            // File 객체의 URI 를 얻는다.
            cameraImageUri = FileProvider.getUriForFile(
                activity,
                activity.packageName + ".fileprovider",
                file
            )
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
            return intentCamera
        } else {
            return null
        }
    }

    private fun videoCaptureIntent(): Intent? {
        val intentCamera = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (intentCamera.resolveActivity(activity.packageManager) != null) {
            val path = activity.cacheDir
            val tempPath = File(path, "/temp_video")
            if (!tempPath.exists()) {
                if (!tempPath.mkdirs()) {
                    Timber.e("mkdirs fail ${tempPath.absolutePath}")
                }
            } else {
                tempPath.listFiles { pathname ->
                    pathname?.isFile == true && pathname.absolutePath.contains(
                        Companion.PREFIX,
                        true
                    )
                }?.forEach { file ->
                    if (!file.delete()) {
                        Timber.e("delete fail ${file.absolutePath}")
                    }
                }
            }
            val file = File(tempPath, Companion.PREFIX + System.currentTimeMillis() + ".mp4")
            // File 객체의 URI 를 얻는다.
            cameraVideoUri = FileProvider.getUriForFile(
                activity,
                activity.packageName + ".fileprovider",
                file
            )
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, cameraVideoUri)
            return intentCamera
        } else {
            return null
        }
    }

    private fun audioCaptureIntent(): Intent? {
        // NOTE: Sound recorder does not support EXTRA_OUTPUT
        // TODO: 삼성폰에서는 결과가 리턴된다. (LG폰은 안되었음)
        val intentRecordSound = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        return if (intentRecordSound.resolveActivity(activity.packageManager) != null) {
            intentRecordSound
        } else {
            null
        }
    }

    companion object {

        /**
         * 캡쳐가 껴져 있지만 타입 정의가 없을 경우 캡쳐 포함 여부
         */
        private const val shouldUseCaptureForEmptyTypes = false

        /**
         * 캡쳐가 껴져 있지만 타입 정의가 All(*\/\*) 일 경우 캡쳐 포함 여부
         */
        private const val shouldUseCaptureForAllTypes = false
        private const val PREFIX = "Camera_"
    }
}