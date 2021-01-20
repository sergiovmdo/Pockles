package com.pes.pockles.util.extensions

import androidx.lifecycle.MediatorLiveData

fun <T> MediatorLiveData<T>.forceRefresh() {
    this.value = this.value
}