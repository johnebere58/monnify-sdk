package com.teamapt.monnify.monnify_payment_sdk.rest.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class InitializeUssdPaymentResponse(

    @SerializedName("ussdCode")
    @Expose
    var ussdCode: String?,

    @SerializedName("paymentCode")
    @Expose
    var paymentCode: String?,

    @SerializedName("providerReference")
    @Expose
    var providerReference: String?,

    @SerializedName("authorizedAmount")
    @Expose
    var authorizedAmount: BigDecimal?,

    @SerializedName("transactionReference")
    @Expose
    var transactionReference: String?
)
