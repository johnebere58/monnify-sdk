package com.monnify.monnify_payment_sdk.rest.data.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.monnify.monnify_payment_sdk.util.Constants

data class CardPaymentRequest(
    @SerializedName("transactionReference")
    @Expose
    var transactionReference: String? = null,

    @SerializedName("apiKey")
    @Expose
    var apiKey: String? = null,

    @SerializedName("collectionChannel")
    @Expose
    var collectionChannel: String? = null,

    @SerializedName("card")
    @Expose
    var card: Card? = null
) {
    constructor(transactionReference: String?, apiKey: String?, card: Card?) : this(
        transactionReference = transactionReference,
        apiKey = apiKey,
        collectionChannel = Constants.COLLECTION_CHANNEL,
        card = card
    )
}
