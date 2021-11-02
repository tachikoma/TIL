package kr.ds.helper.util

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import timber.log.Timber

typealias PermissionGrantedCallback = (() -> Unit)
/**
 * parameter shouldShowRequestPermissionRationale: Boolean
 */
typealias PermissionDeniedCallback = ((Boolean) -> Unit)
typealias MultiplePermissionDeniedCallback = ((Array<String>) -> Unit)

class PermissionGrantHelper(private val activity: ComponentActivity) {

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Timber.d("권한 승인됨")
                permissionGrantedCallback?.invoke()
            } else {
                val shouldShowRequestPermissionRationale =
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        requestedPermission
                    )
                Timber.d("권한 거부됨 Rationale:$shouldShowRequestPermissionRationale")
                permissionDeniedCallback?.invoke(shouldShowRequestPermissionRationale)
            }
        }

    private val requestMultiplePermissionsLauncher =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { requestedPermissionMaps ->
            if (requestedPermissionMaps.isEmpty()) {
                Timber.d("권한 취소 됨")
                return@registerForActivityResult
            }
            if (null == requestedPermissionMaps.values.firstOrNull { it == false }) {
                Timber.d("권한 모두 승인됨")
                permissionGrantedCallback?.invoke()
            } else {
                handlePermissionsDenied(requestedPermissionMaps)
            }
        }

    private fun handlePermissionsDenied(requestedPermissionMaps: Map<String, Boolean>) {
        val denied = requestedPermissionMaps.filter { !it.value }

        val rationales = denied.filterKeys { key ->
            !ActivityCompat.shouldShowRequestPermissionRationale(
                activity, key
            )
        }
        if (rationales.isEmpty()) {
            requestMultiplePermissionsLauncher.launch(denied.keys.toTypedArray())
        } else {
            Timber.d("권한 거부됨 Rationales:${rationales.keys}")
            multiplePermissionDeniedCallback?.invoke(rationales.keys.toTypedArray())
        }
    }

    private var requestedPermission: String = ""
    private var permissionGrantedCallback: PermissionGrantedCallback? = null
    private var permissionDeniedCallback: PermissionDeniedCallback? = null


    private var multiplePermissionDeniedCallback: MultiplePermissionDeniedCallback? = null

    private fun checkPermission(permission: String): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            activity,
            permission
        )
    }

    fun checkPermissionAndAction(
        permission: String,
        grantedCallback: PermissionGrantedCallback,
        deniedCallback: PermissionDeniedCallback? = null
    ) {
        requestedPermission = permission
        permissionGrantedCallback = grantedCallback
        permissionDeniedCallback = deniedCallback

        when {
            checkPermission(permission) -> {
                grantedCallback()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
                requestPermission(permission)
            }
            else -> {
                requestPermission(permission)
            }
        }
    }

    private fun requestPermission(
        permission: String
    ) {
        requestPermissionLauncher.launch(permission)
    }

    fun checkMultiplePermissionsAndAction(
        permissions: Array<out String>,
        grantedCallback: PermissionGrantedCallback,
        multipleDeniedCallback: MultiplePermissionDeniedCallback? = null
    ) {
        permissionGrantedCallback = grantedCallback
        multiplePermissionDeniedCallback = multipleDeniedCallback

        requestMultiplePermissionsLauncher.launch(permissions)
    }
}