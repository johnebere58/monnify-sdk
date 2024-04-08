package com.teamapt.monnify.monnify_payment_sdk.rest.data.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.teamapt.monnify.monnify_payment_sdk.util.Constants

data class AuthorizeCardOtpRequest(
    @SerializedName("transactionReference")
    @Expose
    var transactionReference: String? = null,

    @SerializedName("apiKey")
    @Expose
    var apiKey: String? = null,

    @SerializedName("collectionChannel")
    @Expose
    var collectionChannel: String?,

    @SerializedName("tokenId")
    @Expose
    var tokenId: String? = null,

    @SerializedName("token")
    @Expose
    var token: String? = null
) {
    constructor(
        transactionReference: String?,
        apiKey: String?,
        token: String?,
        tokenId: String?
    ) : this(
        transactionReference = transactionReference,
        apiKey = apiKey,
        collectionChannel = Constants.COLLECTION_CHANNEL,
        token = token,
        tokenId = tokenId
    )
}
