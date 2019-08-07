package com.ckdroid.geofirequery.model

import com.ckdroid.geofirequery.utils.BoundingBoxUtils

/**
@link[Distance] class represents the distance in radius and @link[DistanceUnit] of KM or MILES
 */
class Distance(
    val distance: Double,
    val unit: BoundingBoxUtils.DistanceUnit
)