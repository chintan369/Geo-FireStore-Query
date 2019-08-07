package com.ckdroid.geofirequery.model

/**
 * @link[BoundingBox] class provides the minimumMatch and maximumMatch based on calculated minimum and maximum latLng by [BoundingBoxUtils]
 */

class BoundingBox(minimumLatitude: Double, minimumLongitude: Double, maximumLatitude: Double, maximumLongitude: Double) {
    private val minimumLatitude: Double = Math.toDegrees(minimumLatitude)
    private val minimumLongitude: Double = Math.toDegrees(minimumLongitude)
    private val maximumLatitude: Double = Math.toDegrees(maximumLatitude)
    private val maximumLongitude: Double = Math.toDegrees(maximumLongitude)
    val minimumMatch: Double
        get() = (this.minimumLatitude + 90) * 180 + this.minimumLongitude
    val maximumMatch: Double
        get() = (this.maximumLatitude + 90) * 180 + this.maximumLongitude
}