package com.monnify.monnify_payment_sdk.rest.data.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BankPaymentVerifyRequest(

    @SerializedName("transactionReference")
    @Expose
    var transactionReference: String? = null,

    @SerializedName("accountNumber")
    @Expose
    var accountNumber: String? = null,

    @SerializedName("bankCode")
    @Expose
    var bankCode: String? = null
)
