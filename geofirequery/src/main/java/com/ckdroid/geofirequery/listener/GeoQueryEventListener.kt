package com.ckdroid.geofirequery.listener

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException

interface GeoQueryEventListener {
    fun onEvent(
        fireStoreException: FirebaseFirestoreException? = null,
        addedAndModifiedData: MutableList<DocumentSnapshot> = mutableListOf(),
        removedData: MutableList<DocumentSnapshot> = mutableListOf()
    )
}