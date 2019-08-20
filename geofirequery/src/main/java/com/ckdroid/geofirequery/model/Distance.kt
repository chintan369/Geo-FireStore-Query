package com.ckdroid.geofirequery.model

import com.ckdroid.geofirequery.utils.BoundingBoxUtils

/**
@link[Distance] class represents the distance in radius and @link[DistanceUnit] of KM or MILES
 */
class Distance(
    val distance: Double,
    val unit: BoundingBoxUtils.DistanceUnit
) {
    val distanceInMeters: Double
        get() {
            return when (unit) {
                BoundingBoxUtils.DistanceUnit.MILES -> {
                    distance.times(1000).times(MILES_TO_KM)
                }
                BoundingBoxUtils.DistanceUnit.KILOMETERS -> {
                    distance.times(1000)
                }
            }
        }

    companion object {
        private const val MILES_TO_KM = 1.60934
    }
}