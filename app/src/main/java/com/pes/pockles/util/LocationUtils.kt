package com.pes.pockles.util

import android.app.Activity
import android.location.Location
import com.google.android.gms.location.LocationServices

class LocationUtils {
    companion object {
        fun getLastLocation(
            activity: Activity,
            onSuccess: (Location) -> Unit,
            onError: (Exception?) -> Unit
        ) {
            val locationClient = LocationServices.getFusedLocationProviderClient(activity)

            locationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) onSuccess(location) else onError(null)
            }.addOnFailureListener { e ->
                onError(e)
            }
        }
    }
}
