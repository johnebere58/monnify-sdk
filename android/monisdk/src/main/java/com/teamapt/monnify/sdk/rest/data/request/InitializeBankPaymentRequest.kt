package com.teamapt.monnify.sdk.rest.data.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class InitializeBankPaymentRequest(

    @SerializedName("transactionReference")
    @Expose
    var transactionReference: String?,

    @SerializedName("apiKey")
    @Expose
    val apiKey: String?,

    @SerializedName("collectionChannel")
    val collectionChannel: String?
)
