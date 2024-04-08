package com.teamapt.monnify.sdk.util

import androidx.lifecycle.MutableLiveData
import android.text.Editable

fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { setValue(initialValue) }