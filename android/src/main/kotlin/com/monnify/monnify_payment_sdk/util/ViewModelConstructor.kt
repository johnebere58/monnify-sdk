package com.monnify.monnify_payment_sdk.util

import androidx.lifecycle.ViewModel

interface ViewModelConstructor {
    fun create(): ViewModel
}
