package com.monnify.monnify_payment_sdk.rest.data.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.monnify.monnify_payment_sdk.util.Constants

data class BankPaymentAuthorizeRequest(

    @SerializedName("transactionReference")
    @Expose
    val transactionReference: String?,

    @SerializedName("providerReference")
    @Expose
    val providerReference: String?,

    @SerializedName("apiKey")
    @Expose
    val apiKey: String?,

    @SerializedName("collectionChannel")
    @Expose
    val collectionChannel: String = Constants.COLLECTION_CHANNEL,

    @SerializedName("token")
    @Expose
    val token: String?
)
