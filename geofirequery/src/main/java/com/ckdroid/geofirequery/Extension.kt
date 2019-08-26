package com.ckdroid.geofirequery

import android.location.Location
import com.ckdroid.geofirequery.model.Distance
import com.ckdroid.geofirequery.model.QueryLocation.Companion.DEFAULT_KEY_GEO_DEGREE_MATCH
import com.ckdroid.geofirequery.model.QueryLocation.Companion.KEY_GEO_LOCATION
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions

internal fun DocumentSnapshot.isInGivenDistance(currentLocation: Location?, distance: Distance?): Boolean {
    val documentLocation: GeoPoint? = this.getGeoPoint(KEY_GEO_LOCATION)
    if (documentLocation != null) {
        val location = Location("")
        location.latitude = documentLocation.latitude
        location.longitude = documentLocation.longitude

        val distanceInMeters = location.distanceTo(currentLocation).toDouble()

        return distanceInMeters <= (distance?.distanceInMeters ?: 0.0)
    }
    return false
}

fun DocumentReference.setLocation(
    latitude: Double,
    longitude: Double,
    fieldName: String = DEFAULT_KEY_GEO_DEGREE_MATCH,
    useUpdateMethod: Boolean = true
): Task<Void> {
    val degreeMatch = (latitude + 90) * 180 + longitude
    val mapData: HashMap<String, Any> = hashMapOf()
    mapData[fieldName] = degreeMatch
    mapData[KEY_GEO_LOCATION] = GeoPoint(latitude, longitude)
    return if (useUpdateMethod) this.update(mapData) else this.set(mapData, SetOptions.merge())
}