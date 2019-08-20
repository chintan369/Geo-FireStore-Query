package com.ckdroid.geofirequery.utils

import com.ckdroid.geofirequery.model.BoundingBox
import com.ckdroid.geofirequery.model.QueryLocation
import com.google.firebase.firestore.GeoPoint
import kotlin.math.*

/**
 * @link[BoundingBoxUtils] class is a util class to create the bounding box for the given radius from the given center location so can find the items falls under that bounds
 */

class BoundingBoxUtils(private val distanceUnit: DistanceUnit) {

    enum class DistanceUnit {
        MILES,
        KILOMETERS;
    }

    /**
     * @param queryLocation the query latitude and longitude
     * @param distance the distance for the bounding box
     */
    fun getBoundingBox(queryLocation: QueryLocation, distance: Double): BoundingBox {

        if (distance < 0.0) { //Distance must be greater than or equals to 0
            throw IllegalArgumentException()
        }

        val distanceInRadians: Double = when (distanceUnit) {
            DistanceUnit.MILES -> distance / EARTH_RADIUS_MILES
            DistanceUnit.KILOMETERS -> distance / EARTH_RADIUS_KM
        }

        var minimumLatitude = queryLocation.latitude - distanceInRadians
        var maximumLatitude = queryLocation.latitude + distanceInRadians
        var minimumLongitude: Double
        var maximumLongitude: Double

        if (minimumLatitude > MINIMUM_LATITUDE && maximumLatitude < MAXIMUM_LATITUDE) {

            val deltaLongitude = asin(sin(distanceInRadians) / cos(queryLocation.latitude))

            minimumLongitude = queryLocation.longitude - deltaLongitude
            if (minimumLongitude < MINIMUM_LONGITUDE) {
                minimumLongitude += 2.0 * Math.PI
            }

            maximumLongitude = queryLocation.longitude + deltaLongitude
            if (maximumLongitude > MAXIMUM_LONGITUDE) {
                maximumLongitude -= 2.0 * Math.PI
            }

        } else {
            minimumLatitude = max(minimumLatitude, MINIMUM_LATITUDE)
            maximumLatitude = min(maximumLatitude, MAXIMUM_LATITUDE)
            minimumLongitude = MINIMUM_LONGITUDE
            maximumLongitude = MAXIMUM_LONGITUDE
        }

        return BoundingBox(minimumLatitude, minimumLongitude, maximumLatitude, maximumLongitude)
    }

    fun getBoundingBoxForNew(queryLocation: QueryLocation, distance: Double): BoundingBox {
        val nearByLocationList: MutableList<GeoPoint> = mutableListOf()

        (1..200).forEach { _ ->
            nearByLocationList.add(pointAtDistance(queryLocation, distance))
        }

        if (nearByLocationList.isNotEmpty()) {
            val sortedBounds = nearByLocationList.sortedWith(compareBy({ it.latitude }, { it.longitude }))

            val minimumGeoPoint = sortedBounds.first()

            val maximumGeoPoint = sortedBounds.last()

            return BoundingBox(
                minimumGeoPoint.latitude,
                minimumGeoPoint.longitude,
                maximumGeoPoint.latitude,
                maximumGeoPoint.longitude
            )
        }

        return BoundingBox(
            queryLocation.latitude,
            queryLocation.longitude,
            queryLocation.latitude,
            queryLocation.longitude
        )
    }

    private fun pointAtDistance(queryLocation: QueryLocation, distance: Double): GeoPoint {
        val sinLat = Math.sin(queryLocation.latitude)
        val cosLat = Math.cos(queryLocation.latitude)

        val bearing = Math.random().times(TWO_PI)
        val theta = when (distanceUnit) {
            DistanceUnit.KILOMETERS -> distance.div(EARTH_RADIUS_KM)
            DistanceUnit.MILES -> distance.div(EARTH_RADIUS_MILES)
        }

        val sinBearing = sin(bearing)
        val cosBearing = cos(bearing)
        val sinTheta = sin(theta)
        val cosTheta = cos(theta)

        val latitude = asin(sinLat * cosTheta + cosLat * sinTheta * cosBearing)
        var longitude =
            queryLocation.longitude + atan2(sinBearing * sinTheta * cosLat, cosTheta - sinLat * sin(latitude))
        longitude = ((longitude + THREE_PI) % TWO_PI) - Math.PI

        return GeoPoint(Math.toDegrees(latitude), Math.toDegrees(longitude))
    }

    companion object {

        private val EARTH_RADIUS_KM = 6371.000//6371.001
        private val EARTH_RADIUS_MILES = 3958.756

        private val THREE_PI = Math.PI.times(3)
        private val TWO_PI = Math.PI.times(2)

        private val MINIMUM_LATITUDE = Math.toRadians(-90.0)  // -PI/2
        private val MAXIMUM_LATITUDE = Math.toRadians(90.0)   //  PI/2
        private val MINIMUM_LONGITUDE = Math.toRadians(-180.0) // -PI
        private val MAXIMUM_LONGITUDE = Math.toRadians(180.0)  //  PI
    }

}