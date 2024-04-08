package com.teamapt.monnify.sdk.rest.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class InitializeBankPaymentResponse(

    @SerializedName("transactionReference")
    @Expose
    var transactionReference: String? = null,

    @SerializedName("totalAmountPayable")
    @Expose
    var totalAmountPayable: BigDecimal = BigDecimal.ZERO,

    @SerializedName("amount")
    @Expose
    var amount: BigDecimal = BigDecimal.ZERO,

    @SerializedName("paymentFee")
    @Expose
    var paymentFee: BigDecimal = BigDecimal.ZERO
)