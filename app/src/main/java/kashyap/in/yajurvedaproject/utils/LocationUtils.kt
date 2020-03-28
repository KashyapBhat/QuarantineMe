package kashyap.`in`.yajurvedaproject.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import android.preference.PreferenceManager
import androidx.annotation.RequiresPermission
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

class LocationUtils(val context: Context, private val mLocationCallback: LocationCallbacks) :
    PreferenceManager.OnActivityResultListener {

    private val UPDATE_INTERVAL_IN_MILLISECONDS = 10 * 1000L // every 10 secs

    private var mLocationRequest: LocationRequest? = null
    private var mFusedLocationClient: FusedLocationProviderClient? =
        FusedLocationProviderClient(context)
    private val locationCallback = object : LocationCallback() {
        @SuppressLint("MissingPermission")
        override fun onLocationResult(locationResult: LocationResult) {
            getCurrentLocation(true)
        }

        @SuppressLint("MissingPermission")
        override fun onLocationAvailability(locationAvailable: LocationAvailability?) {
            super.onLocationAvailability(locationAvailable)
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun initiateCurrentLocation() {
        createLocationRequest()
        mLocationCallback.registerActivityResult(this)
        displayLocationSettingsRequest(context)
    }

    private fun createFusedLocationClient() {
        if (mFusedLocationClient == null)
            mFusedLocationClient = FusedLocationProviderClient(context)
        mFusedLocationClient?.requestLocationUpdates(
            mLocationRequest, locationCallback,
            null
        )
    }

    private fun createLocationRequest() {
        if (mLocationRequest == null) {
            mLocationRequest = LocationRequest()
            mLocationRequest!!.interval = UPDATE_INTERVAL_IN_MILLISECONDS
            mLocationRequest!!.fastestInterval = UPDATE_INTERVAL_IN_MILLISECONDS
            mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun displayLocationSettingsRequest(context: Context) {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest!!)
        val client = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { getCurrentLocation(true) }
        if (context is Activity)
            task.addOnFailureListener(context) { e ->
                if (e is ApiException)
                    when (e.statusCode) {
                        CommonStatusCodes.RESOLUTION_REQUIRED ->
                            // TestCenterLocation settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                val resolvable = e as ResolvableApiException
                                resolvable.startResolutionForResult(context, REQUEST_CHECK_SETTINGS)
                            } catch (sendEx: IntentSender.SendIntentException) {
                                // Ignore the error.
                            }

                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->
                            // TestCenterLocation settings are not satisfied. However, we have no way
                            // to fix the settings so we won't show the dialog.
                            mLocationCallback.onGpsUnavailable()
                    }
            }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun getCurrentLocation(canGetLocation: Boolean = true) {
        if (canGetLocation && context is Activity) {
            mLocationCallback.showProgress()
            createFusedLocationClient()
            mFusedLocationClient!!
                .lastLocation
                .addOnCompleteListener {
                    when (it.isSuccessful) {
                        true -> {
                            if (it.result == null) {
                                createFusedLocationClient()
                            } else {
                                mFusedLocationClient!!.removeLocationUpdates(locationCallback)
                                mLocationCallback.onLocationResult(it.result)
                                mLocationCallback.hideProgress()
                            }
                        }
                        false -> mLocationCallback.onLocationResult(null)
                    }
                }
        } else mLocationCallback.onLocationResult(null)
    }

    @SuppressLint("MissingPermission")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                getCurrentLocation()
            } else {
                mLocationCallback.onGpsUnavailable()
            }
            return true
        }
        return false
    }
}

const val REQUEST_CHECK_SETTINGS = 1234

interface LocationCallbacks {
    fun showProgress()

    fun hideProgress()

    fun onLocationResult(location: Location?)

    fun onGpsUnavailable()

    fun registerActivityResult(onActivityResultListener: PreferenceManager.OnActivityResultListener)
}
