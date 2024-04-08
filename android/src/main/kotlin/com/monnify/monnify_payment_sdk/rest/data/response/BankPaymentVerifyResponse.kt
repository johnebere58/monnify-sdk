package com.monnify.monnify_payment_sdk.rest.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BankPaymentVerifyResponse(

    @SerializedName("additionalInfoRequired")
    @Expose
    var additionalInfoRequired: Boolean?,

    @SerializedName("accountNumber")
    @Expose
    var accountNumber: String?,

    @SerializedName("transactionReference")
    @Expose
    var transactionReference: String?,

    @SerializedName("accountName")
    @Expose
    var accountName: String?
)