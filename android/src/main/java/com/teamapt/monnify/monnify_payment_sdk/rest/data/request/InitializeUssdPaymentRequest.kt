package com.teamapt.monnify.monnify_payment_sdk.rest.data.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class InitializeUssdPaymentRequest(
    @SerializedName("transactionReference")
    @Expose
    val transactionReference: String? = null,

    @SerializedName("bankUssdCode")
    @Expose
    val bankUssdCode: String? = null
)