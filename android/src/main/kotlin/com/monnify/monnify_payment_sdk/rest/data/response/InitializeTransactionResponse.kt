package com.monnify.monnify_payment_sdk.rest.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class InitializeTransactionResponse(
    @SerializedName("transactionReference")
    @Expose
    var transactionReference: String = "",

    @SerializedName("merchantName")
    @Expose
    var merchantName: String = "",

    @SerializedName("merchantLogoUrl")
    @Expose
    var merchantLogoUrl: String? = null,

    @SerializedName("enabledPaymentMethod")
    @Expose
    var enabledPaymentMethod: List<String>? = null
)