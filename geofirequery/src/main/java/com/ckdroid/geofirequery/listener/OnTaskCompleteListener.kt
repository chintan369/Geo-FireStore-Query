package com.ckdroid.geofirequery.listener

import com.google.firebase.firestore.DocumentSnapshot

interface OnTaskCompleteListener {
    fun onFailure(ex: Exception)
    fun onSuccess(addedOrModifiedList: MutableList<DocumentSnapshot>, removedList: MutableList<DocumentSnapshot>)
}