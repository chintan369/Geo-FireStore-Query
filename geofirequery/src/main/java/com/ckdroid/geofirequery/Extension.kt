package com.ckdroid.geofirequery

import com.ckdroid.geofirequery.model.Distance
import com.ckdroid.geofirequery.model.QueryLocation
import com.ckdroid.geofirequery.model.QueryLocation.Companion.DEFAULT_KEY_GEO_DEGREE_MATCH
import com.ckdroid.geofirequery.utils.BoundingBoxUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions


fun Query.whereNearToLocation(
    queryAtLocation: QueryLocation,
    distance: Distance,
    fieldName: String = DEFAULT_KEY_GEO_DEGREE_MATCH
): Query {
    val geoPointUtils = BoundingBoxUtils(distance.unit)
    val boundingBox = geoPointUtils.getBoundingBox(queryAtLocation, distance.distance)
    return orderBy(fieldName)
        .whereGreaterThanOrEqualTo(fieldName, boundingBox.minimumMatch)
        .whereLessThanOrEqualTo(fieldName, boundingBox.maximumMatch)
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
    return if (useUpdateMethod) this.update(mapData) else this.set(mapData, SetOptions.merge())
}