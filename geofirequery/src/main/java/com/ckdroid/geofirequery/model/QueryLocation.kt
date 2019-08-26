package com.ckdroid.geofirequery.model

class QueryLocation {

    var latitude: Double = 0.toDouble()
        private set
    var longitude: Double = 0.toDouble()
        private set

    private fun checkBounds() {
        if (latitude < MINIMUM_LATITUDE || latitude > MAXIMUM_LATITUDE ||
            longitude < MINIMUM_LONGITUDE || longitude > MAXIMUM_LONGITUDE
        ) {
            throw IllegalArgumentException()

        }
    }

    companion object {

        private val MINIMUM_LATITUDE = Math.toRadians(-90.0)  // -PI/2
        private val MAXIMUM_LATITUDE = Math.toRadians(90.0)   //  PI/2
        private val MINIMUM_LONGITUDE = Math.toRadians(-180.0) // -PI
        private val MAXIMUM_LONGITUDE = Math.toRadians(180.0)  //  PI

        const val DEFAULT_KEY_GEO_DEGREE_MATCH = "g"
        const val KEY_GEO_LOCATION = "geoLocation"

        /**
         * @param latitude the latitude in radians
         * @param longitude the longitude in radians
         */
        fun fromRadians(latitude: Double, longitude: Double): QueryLocation {
            val queryLocation = QueryLocation()
            queryLocation.latitude = latitude
            queryLocation.longitude = longitude
            queryLocation.checkBounds()
            return queryLocation
        }

        /**
         * @param latitude the latitude in degrees.
         * @param longitude the longitude in degrees.
         */
        fun fromDegrees(latitude: Double, longitude: Double): QueryLocation {
            val queryLocation = QueryLocation()
            queryLocation.latitude = Math.toRadians(latitude)
            queryLocation.longitude = Math.toRadians(longitude)
            queryLocation.checkBounds()
            return queryLocation
        }
    }

}