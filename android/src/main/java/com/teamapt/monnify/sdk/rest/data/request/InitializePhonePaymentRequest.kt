package com.teamapt.monnify.sdk.rest.data.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class InitializePhonePaymentRequest(

    @SerializedName("transactionReference")
    @Expose
    val transactionReference: String? = null,

    @SerializedName("phoneNumber")
    @Expose
    val phoneNumber: String? = null
)