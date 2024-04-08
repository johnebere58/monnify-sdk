package com.teamapt.monnify.monnify_payment_sdk.rest.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class InitializePhonePaymentResponse(

    @SerializedName("amount")
    @Expose
    val amount: BigDecimal = BigDecimal.ZERO,

    @SerializedName("phoneNumber")
    @Expose
    val phoneNumber: String? = null,

    @SerializedName("transactionReference")
    @Expose
    val transactionReference: String? = null,

    @SerializedName("status")
    @Expose
    val status: String? = null
)
