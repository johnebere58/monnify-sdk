package com.teamapt.monnify.monnify_payment_sdk.rest.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BankPaymentChargeResponse(

    @SerializedName("status")
    @Expose
    val status: String?,

    @SerializedName("responseDescription")
    @Expose
    val responseDescription: String?,

    @SerializedName("transactionReference")
    @Expose
    val transactionReference: String?,

    @SerializedName("providerReference")
    @Expose
    val providerReference: String?
) {

    enum class Status {
        SUCCESS,
        PENDING,
        FAILED
    }
}