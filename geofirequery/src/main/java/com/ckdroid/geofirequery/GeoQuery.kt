package com.ckdroid.geofirequery

import android.location.Location
import com.ckdroid.geofirequery.listener.GeoQueryEventListener
import com.ckdroid.geofirequery.listener.GeoQueryTask
import com.ckdroid.geofirequery.model.Distance
import com.ckdroid.geofirequery.model.QueryLocation
import com.ckdroid.geofirequery.utils.BoundingBoxUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*

class GeoQuery {

    var firestore = FirebaseFirestore.getInstance()

    lateinit var query: Query

    //Distance and center location to calculate the distance after data received
    var distance: Distance? = null
    var currentLocation: Location? = null

    //To add listener for snapshot listener
    var queryListener: ListenerRegistration? = null

    var querySnapshotTask: Task<QuerySnapshot>? = null

    fun collection(collectionName: String): GeoQuery {
        if (collectionName.isEmpty()) {
            throw Exception("Collection name must not be empty!")
        }
        this.query = firestore.collection(collectionName)
        return this
    }

    fun whereArrayContains(field: String, value: Any): GeoQuery {
        this.query = this.query.whereArrayContains(field, value)
        return this
    }

    fun whereArrayContains(fieldPath: FieldPath, value: Any): GeoQuery {
        this.query = this.query.whereArrayContains(fieldPath, value)
        return this
    }

    fun whereEqualTo(field: String, value: Any): GeoQuery {
        this.query = this.query.whereEqualTo(field, value)
        return this
    }

    fun whereEqualTo(fieldPath: FieldPath, value: Any): GeoQuery {
        this.query = this.query.whereEqualTo(fieldPath, value)
        return this
    }

    fun whereGreaterThan(field: String, value: Any): GeoQuery {
        this.query = this.query.whereGreaterThan(field, value)
        return this
    }

    fun whereGreaterThan(fieldPath: FieldPath, value: Any): GeoQuery {
        this.query = this.query.whereGreaterThan(fieldPath, value)
        return this
    }

    fun whereGreaterThanOrEqualTo(field: String, value: Any): GeoQuery {
        this.query = this.query.whereGreaterThanOrEqualTo(field, value)
        return this
    }

    fun whereGreaterThanOrEqualTo(fieldPath: FieldPath, value: Any): GeoQuery {
        this.query = this.query.whereGreaterThanOrEqualTo(fieldPath, value)
        return this
    }

    fun whereLessThan(field: String, value: Any): GeoQuery {
        this.query = this.query.whereLessThan(field, value)
        return this
    }

    fun whereLessThan(fieldPath: FieldPath, value: Any): GeoQuery {
        this.query = this.query.whereLessThan(fieldPath, value)
        return this
    }

    fun whereLessThanOrEqualTo(field: String, value: Any): GeoQuery {
        this.query = this.query.whereLessThanOrEqualTo(field, value)
        return this
    }

    fun whereLessThanOrEqualTo(fieldPath: FieldPath, value: Any): GeoQuery {
        this.query = this.query.whereLessThanOrEqualTo(fieldPath, value)
        return this
    }

    fun orderBy(field: String): GeoQuery {
        this.query = this.query.orderBy(field)
        return this
    }

    fun orderBy(fieldPath: FieldPath): GeoQuery {
        this.query = this.query.orderBy(fieldPath)
        return this
    }

    fun orderBy(field: String, direction: Query.Direction): GeoQuery {
        this.query = this.query.orderBy(field, direction)
        return this
    }

    fun orderBy(fieldPath: FieldPath, direction: Query.Direction): GeoQuery {
        this.query = this.query.orderBy(fieldPath, direction)
        return this
    }

    fun limit(limit: Long): GeoQuery {
        this.query = this.query.limit(limit)
        return this
    }

    fun endAt(vararg fieldValues: Any): GeoQuery {
        this.query = this.query.endAt(*fieldValues)
        return this
    }

    fun endAt(documentSnapshot: DocumentSnapshot): GeoQuery {
        this.query = this.query.endAt(documentSnapshot)
        return this
    }

    fun startAt(vararg fieldValues: Any): GeoQuery {
        this.query = this.query.startAt(*fieldValues)
        return this
    }

    fun startAt(documentSnapshot: DocumentSnapshot): GeoQuery {
        this.query = this.query.startAt(documentSnapshot)
        return this
    }

    fun endBefore(vararg fieldValues: Any): GeoQuery {
        this.query = this.query.endBefore(*fieldValues)
        return this
    }

    fun endBefore(documentSnapshot: DocumentSnapshot): GeoQuery {
        this.query = this.query.endBefore(documentSnapshot)
        return this
    }

    fun startAfter(vararg fieldValues: Any): GeoQuery {
        this.query = this.query.startAfter(*fieldValues)
        return this
    }

    fun startAfter(documentSnapshot: DocumentSnapshot): GeoQuery {
        this.query = this.query.startAfter(documentSnapshot)
        return this
    }

    fun whereNearToLocation(
        centerLocation: Location,
        distance: Distance,
        onFieldName: String = QueryLocation.DEFAULT_KEY_GEO_DEGREE_MATCH
    ): GeoQuery {
        this.currentLocation = centerLocation
        this.distance = distance
        val queryLocation = QueryLocation.fromDegrees(centerLocation.latitude, centerLocation.longitude)
        this.query = this.query.whereNearToLocation(queryLocation, distance, onFieldName)
        return this
    }

    fun stopListeningData() {
        this.queryListener?.remove()
    }

    fun addSnapshotListener(snapshotListener: (FirebaseFirestoreException?, MutableList<DocumentSnapshot>, MutableList<DocumentSnapshot>) -> Unit) {
        this.queryListener = query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->

            val addedOrModifiedList: MutableList<DocumentSnapshot> = mutableListOf()
            val removedList: MutableList<DocumentSnapshot> = mutableListOf()

            querySnapshot?.let {
                removedList.addAll(it.documentChanges.filter { documentChange -> documentChange.type == DocumentChange.Type.REMOVED }.map { documentChange -> documentChange.document }.toMutableList())

                val nonRemovedList =
                    it.documentChanges.filter { documentChange -> documentChange.type != DocumentChange.Type.REMOVED }
                        .map { documentChange -> documentChange.document }.toMutableList()

                nonRemovedList.forEach { document ->
                    if (distance != null && currentLocation != null) {
                        if (document.isInGivenDistance(currentLocation, distance)) {
                            addedOrModifiedList.add(document)
                        } else {
                            removedList.add(document)
                        }
                    } else {
                        addedOrModifiedList.add(document)
                    }
                }
            }
            snapshotListener.invoke(firebaseFirestoreException, addedOrModifiedList, removedList)
        }
    }

    fun addSnapshotListener(snapshotListener: GeoQueryEventListener) {
        addSnapshotListener { firebaseFirestoreException, addedOrModifiedList, removeList ->
            snapshotListener.onEvent(firebaseFirestoreException, addedOrModifiedList, removeList)
        }
    }

    fun get(): GeoQueryTask {
        querySnapshotTask = this.query.get()
        return GeoQueryTask(this)
    }

    private fun Query.whereNearToLocation(
        queryAtLocation: QueryLocation,
        distance: Distance,
        fieldName: String = QueryLocation.DEFAULT_KEY_GEO_DEGREE_MATCH
    ): Query {
        val geoPointUtils = BoundingBoxUtils(distance.unit)
        val boundingBox = geoPointUtils.getBoundingBox(queryAtLocation, distance.distance)
        return orderBy(fieldName)
            .whereGreaterThanOrEqualTo(fieldName, boundingBox.minimumMatch)
            .whereLessThanOrEqualTo(fieldName, boundingBox.maximumMatch)
    }

}