package com.teamapt.monnify.monnify_payment_sdk.rest.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class InitializeCardPaymentResponse(
    @SerializedName("totalAmountPayable")
    @Expose
    var totalAmountPayable: BigDecimal?,

    @SerializedName("amount")
    @Expose
    var amount: BigDecimal?,

    @SerializedName("paymentFee")
    @Expose
    var paymentFee: BigDecimal?,

    @SerializedName("transactionReference")
    @Expose
    var transactionReference: String?
)