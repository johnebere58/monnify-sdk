package com.teamapt.monnify.sdk.rest.data.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.teamapt.monnify.sdk.util.Constants

data class InitializeCardPaymentRequest(
    @SerializedName("transactionReference")
    @Expose
    var transactionReference: String?,
    @SerializedName("apiKey")
    @Expose
    var apiKey: String?,
    @SerializedName("collectionChannel")
    @Expose
    var collectionChannel: String = Constants.COLLECTION_CHANNEL
)