package com.ckdroid.geofirequery.listener

import com.ckdroid.geofirequery.GeoQuery
import com.ckdroid.geofirequery.isInGivenDistance
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot

/**
 *
 * This class GeoQueryTask is only will be accessible when user needs to get data through get() method from firestore query. So this will give access to [addOnCompleteListener], [addOnSuccessListener] and [addOnFailureListener] only after calling [GeoQuery.get] method.
 */
class GeoQueryTask internal constructor(query: GeoQuery) {

    private val geoQuery = query

    /**
     * FUNCTION COMMENT
     *
     * add task's onComplete listener to get exception or data in a single function
     * @param onCompleteListener as lambda function
     * @return none
     */
    fun addOnCompleteListener(onCompleteListener: (Exception?, MutableList<DocumentSnapshot>, MutableList<DocumentSnapshot>) -> Unit) {
        if (geoQuery.querySnapshotTask == null) {
            throw Exception("You must call get() method before calling addOnCompleteListener() method!")
        }
        geoQuery.querySnapshotTask?.let {
            it.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val exception = task.exception
                    onCompleteListener.invoke(exception, mutableListOf(), mutableListOf())
                } else {

                    val addedOrModifiedList: MutableList<DocumentSnapshot> = mutableListOf()
                    val removedList: MutableList<DocumentSnapshot> = mutableListOf()

                    task.result?.let { querySnapshot ->
                        removedList.addAll(querySnapshot.documentChanges.filter { documentChange -> documentChange.type == DocumentChange.Type.REMOVED }.map { documentChange -> documentChange.document }.toMutableList())

                        val nonRemovedList =
                            querySnapshot.documentChanges.filter { documentChange -> documentChange.type != DocumentChange.Type.REMOVED }
                                .map { documentChange -> documentChange.document }.toMutableList()

                        nonRemovedList.forEach { document ->
                            if (geoQuery.distance != null && geoQuery.currentLocation != null) {
                                if (document.isInGivenDistance(geoQuery.currentLocation, geoQuery.distance)) {
                                    addedOrModifiedList.add(document)
                                } else {
                                    removedList.add(document)
                                }
                            } else {
                                addedOrModifiedList.add(document)
                            }
                        }
                    }
                    onCompleteListener.invoke(null, addedOrModifiedList, removedList)
                }
            }
        }
    }

    /**
     * FUNCTION COMMENT
     *
     * add task's onComplete listener to get exception or data in a single function
     * @param onCompleteListener [OnTaskCompleteListener] as interface to get data in function
     * @return none
     */
    fun addOnCompleteListener(onCompleteListener: OnTaskCompleteListener) {
        addOnCompleteListener { exception, addedOrModifiedList, removedList ->
            if (exception != null) {
                onCompleteListener.onFailure(exception)
            } else {
                onCompleteListener.onSuccess(addedOrModifiedList, removedList)
            }
        }
    }

    /**
     * FUNCTION COMMENT
     *
     * add task's onSuccess listener to get data in a function
     * @param onSuccessListener as lambda function
     * @return none
     */
    fun addOnSuccessListener(onSuccessListener: (addedOrModifiedList: MutableList<DocumentSnapshot>, removedList: MutableList<DocumentSnapshot>) -> Unit): GeoQueryTask {
        if (geoQuery.querySnapshotTask == null) {
            throw Exception("You must call get() method before calling addOnCompleteListener() method!")
        }
        geoQuery.querySnapshotTask?.let { task ->
            task.addOnSuccessListener {
                val addedOrModifiedList: MutableList<DocumentSnapshot> = mutableListOf()
                val removedList: MutableList<DocumentSnapshot> = mutableListOf()

                task.result?.let { querySnapshot ->
                    removedList.addAll(querySnapshot.documentChanges.filter { documentChange -> documentChange.type == DocumentChange.Type.REMOVED }.map { documentChange -> documentChange.document }.toMutableList())

                    val nonRemovedList =
                        querySnapshot.documentChanges.filter { documentChange -> documentChange.type != DocumentChange.Type.REMOVED }
                            .map { documentChange -> documentChange.document }.toMutableList()

                    nonRemovedList.forEach { document ->
                        if (geoQuery.distance != null && geoQuery.currentLocation != null) {
                            if (document.isInGivenDistance(geoQuery.currentLocation, geoQuery.distance)) {
                                addedOrModifiedList.add(document)
                            } else {
                                removedList.add(document)
                            }
                        } else {
                            addedOrModifiedList.add(document)
                        }
                    }
                }
                onSuccessListener.invoke(addedOrModifiedList, removedList)
            }
        }
        return this
    }

    /**
     * FUNCTION COMMENT
     *
     * add task's onSuccess listener to get data in a function
     * @param onSuccessListener [OnTaskSuccessListener] as interface to get data in a function
     * @return none
     */
    fun addOnSuccessListener(onSuccessListener: OnTaskSuccessListener): GeoQueryTask {
        return addOnSuccessListener { addedOrModifiedList, removedList ->
            onSuccessListener.onSuccess(addedOrModifiedList, removedList)
        }
    }

    /**
     * FUNCTION COMMENT
     *
     * add task's onSuccess listener to get exception in a function
     * @param onFailureListener as lambda function
     * @return none
     */
    fun addOnFailureListener(onFailureListener: (exception: Exception?) -> Unit): GeoQueryTask {
        geoQuery.querySnapshotTask?.let { task ->
            task.addOnFailureListener { exception ->
                onFailureListener.invoke(exception)
            }
        }
        return this
    }

    /**
     * FUNCTION COMMENT
     *
     * add task's onSuccess listener to get data in a function
     * @param onFailureListener [OnTaskFailureListener] as interface to get data in a function
     * @return none
     */
    fun addOnFailureListener(onFailureListener: OnTaskFailureListener): GeoQueryTask {
        return addOnFailureListener { exception ->
            onFailureListener.onFailure(exception)
        }
    }

}