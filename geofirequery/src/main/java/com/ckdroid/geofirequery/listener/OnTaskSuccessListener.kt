package com.ckdroid.geofirequery.listener

import com.google.firebase.firestore.DocumentSnapshot

interface OnTaskSuccessListener {
    fun onSuccess(addedOrModifiedList: MutableList<DocumentSnapshot>, removedList: MutableList<DocumentSnapshot>)
}