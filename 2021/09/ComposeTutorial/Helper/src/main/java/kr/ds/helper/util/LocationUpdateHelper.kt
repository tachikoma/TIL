package kr.ds.helper.util

import android.annotation.SuppressLint
import android.app.Application
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import timber.log.Timber

typealias LocationCallback = (Double, Double) -> Unit

class LocationUpdateHelper(app: Application) {

    private var locationCallback: LocationCallback? = null

    private val mLocationRequest: LocationRequest by lazy {
        LocationRequest.create().apply {
            interval = 5
            fastestInterval = 2
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private val mFusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(app)
    }

    private var mLocationCallback: com.google.android.gms.location.LocationCallback =
        object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                locationCallback?.invoke(location.latitude, location.longitude)
            }
        }

    private fun requestLastLocation() {
        try {
            mFusedLocationClient.lastLocation
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val location = task.result
                        locationCallback?.invoke(location.latitude, location.longitude)
                    } else {
                        Timber.w("Failed to get location.")
                    }
                }
        } catch (unlikely: SecurityException) {
            Timber.e(unlikely, "Lost location permission")
        }
    }

    @SuppressLint("MissingPermission")
    fun startUpdate(locationCallback: LocationCallback) {
        this.locationCallback = locationCallback
        try {
            mFusedLocationClient.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback, Looper.myLooper()
            )
        } catch (unlikely: SecurityException) {
            Timber.e(
                unlikely,
                "Lost location permission. Could not request updates."
            )
        }
        requestLastLocation()
    }

    fun stopUpdate() {
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
        } catch (e: Exception) {
            Timber.e(
                e,
                "Lost location permission. Could not remove updates."
            )
        }
        locationCallback = null
    }
}