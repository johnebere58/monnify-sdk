package com.teamapt.monnify.sdk.rest.data.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.teamapt.monnify.sdk.util.Constants

data class InitializeTransferPaymentRequest(
    @SerializedName("collectionChannel")
    @Expose
    var collectionChannel: String? = Constants.COLLECTION_CHANNEL,

    @SerializedName("transactionReference")
    @Expose
    var transactionReference: String? = null,

    @SerializedName("apiKey")
    @Expose
    var apiKey: String? = null,

    @SerializedName("bankCode")
    @Expose
    var bankCode: String? = null
)