package kr.ds.helper.web

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.webkit.ValueCallback
import android.webkit.WebChromeClient.FileChooserParams
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FileChooseHelper(private val activity: ComponentActivity) {
    private var cameraImageUri: Uri? = null
    private var mCameraPhotoPath: String? = null
    private var cameraVideoUri: Uri? = null
    private var mCameraVideoPath: String? = null
    private var filePathCallbackLollipop: ValueCallback<Array<Uri?>?>? = null
    private var filePathCallbackOldFashion: ValueCallback<Uri?>? = null

    val isOnWorking: Boolean
        get() = null != filePathCallbackLollipop || null != filePathCallbackOldFashion

    private val requestActivityLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Timber.d("lollipop or more. data ${it.data}")
            if (Activity.RESULT_OK == it.resultCode) {
                if (null != filePathCallbackLollipop) {
                    val results = arrayOf(getResultUri(it.data))
                    filePathCallbackLollipop!!.onReceiveValue(results)
                    filePathCallbackLollipop = null
                }
            } else {
                if (null != filePathCallbackLollipop) {
                    filePathCallbackLollipop!!.onReceiveValue(null)
                    filePathCallbackLollipop = null
                }
            }
        } else {
            Timber.d("old fashion data ${it.data}")
            if (Activity.RESULT_OK == it.resultCode) {
                if (null != filePathCallbackOldFashion) {
                    val result = getResultUri(it.data)
                    filePathCallbackOldFashion!!.onReceiveValue(result)
                    filePathCallbackOldFashion = null
                }
            } else {
                if (null != filePathCallbackOldFashion) {
                    filePathCallbackOldFashion!!.onReceiveValue(null)
                    filePathCallbackOldFashion = null
                }
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
            if (mCameraPhotoPath != null) {
                result = Uri.parse(mCameraPhotoPath)
            }
            if (mCameraVideoPath != null) {
                result = Uri.parse(mCameraVideoPath)
            }
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

    private val requestPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {  grantResults ->
        var isAllGranted = true
        if (grantResults.isNotEmpty()) {
            for (value in grantResults.values) {
                if (!value) {
                    isAllGranted = false
                }
            }
        }
        if (isAllGranted) {
            gotoCameraAvailable()
        } else {
            showPermissionPopup(grantResults.keys.toTypedArray())
            if (null != filePathCallbackLollipop) {
                filePathCallbackLollipop!!.onReceiveValue(null)
                filePathCallbackLollipop = null
            }
        }
    }

    private fun showPermissionPopup(permissions: Array<out String>) {
        val context = activity

        if (permissions.isNullOrEmpty()) return

        val shouldShowRequestPermissionRationaleCamera =
            shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)

        var requiredPermissions = ""
        permissions.forEach {
            val isEmptyPermission = requiredPermissions.isEmpty()
            requiredPermissions += when (it) {
                Manifest.permission.CAMERA -> {
                    val string = Manifest.permission.CAMERA
                    if (isEmptyPermission) string else ", $string"
                }
                else -> ""
            }
        }

        if (requiredPermissions.isEmpty()) return

        Timber.d("PermissionRationale camera:$shouldShowRequestPermissionRationaleCamera")
    }

    private fun gotoAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri: Uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }

    var savedFileChooserParams: FileChooserParams? = null
    private fun gotoCameraAvailable() {
        runChooser(savedFileChooserParams)
    }

    fun runImageChooser(
        uriValueCallback: ValueCallback<Uri?>?,
        acceptType: String,
        capture: String
    ) {
        setFilePathCallbackOldFashion(uriValueCallback)
        imageChooser(acceptType, capture)
    }

    private fun imageChooser(acceptType: String, capture: String) {
        var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent!!.resolveActivity(activity.packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath)
            } catch (ex: IOException) {
                // Error occurred while creating the File
                Timber.e(ex, "Unable to create Image File")
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCameraPhotoPath = "file:" + photoFile.absolutePath
                takePictureIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photoFile)
                )
            } else {
                takePictureIntent = null
            }
        }
        val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
        contentSelectionIntent.type = acceptType
        val intentArray: Array<Intent?> = takePictureIntent?.let { arrayOf(it) } ?: arrayOfNulls(0)
        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
        requestActivityLauncher.launch(chooserIntent)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
    }

    private fun setFilePathCallbackOldFashion(filePathCallbackOldFashion: ValueCallback<Uri?>?) {
        if (null != this.filePathCallbackOldFashion) {
            this.filePathCallbackOldFashion!!.onReceiveValue(null)
            this.filePathCallbackOldFashion = null
        }
        this.filePathCallbackOldFashion = filePathCallbackOldFashion
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissionAndRunChooser(fileChooserParams!!)
        } else {
            runChooser(fileChooserParams)
        }
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
                        )
                    )
                }
                if (shouldUseCaptureForAllTypes && 1 == fileChooserParams.acceptTypes.count { type -> type == "*/*" }) {
                    chooserIntent.putExtra(
                        Intent.EXTRA_INITIAL_INTENTS,
                        arrayOf(
                            imageCaptureIntent(),
                            videoCaptureIntent(),
                            audioCaptureIntent()
                        )
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

    private fun arrayOfCaptureIntents(fileChooserParams: FileChooserParams): Array<Intent?> {
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
        return intentArray
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
                        "fokCamera_",
                        true
                    )
                }?.forEach { file ->
                    if (!file.delete()) {
                        Timber.e("delete fail ${file.absolutePath}")
                    }
                }
            }
            val file = File(tempPath, "fokCamera_" + System.currentTimeMillis() + ".jpg")
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
                        "fokCamera_",
                        true
                    )
                }?.forEach { file ->
                    if (!file.delete()) {
                        Timber.e("delete fail ${file.absolutePath}")
                    }
                }
            }
            val file = File(tempPath, "fokCamera_" + System.currentTimeMillis() + ".mp4")
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
        val intentRecordSound = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        return if (intentRecordSound.resolveActivity(activity.packageManager) != null) {
            intentRecordSound
        } else {
            null
        }
    }

    private fun checkPermissionAndRunChooser(fileChooserParams: FileChooserParams) {
        if (fileChooserParams.isCaptureEnabled) {
            val grantedCamera = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
            savedFileChooserParams = fileChooserParams
            if (grantedCamera) {
                gotoCameraAvailable()
            } else {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
            }
        } else {
            runChooser(fileChooserParams)
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
    }
}