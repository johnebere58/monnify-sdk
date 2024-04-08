package com.teamapt.monnify.monnify_payment_sdk.rest.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class StompPaymentAlert(
    @SerializedName("transactionReference")
    @Expose
    var transactionReference: String,

    @SerializedName("paymentReference")
    @Expose
    var paymentReference: String,

    @SerializedName("amountPaid")
    @Expose
    var amountPaid: BigDecimal,

    @SerializedName("totalPayable")
    @Expose
    var totalPayable: BigDecimal,

    @SerializedName("paidOn")
    @Expose
    var paidOn: String,

    @SerializedName("paymentStatus")
    @Expose
    var paymentStatus: String,

    @SerializedName("accountReference")
    @Expose
    var accountReference: Any,

    @SerializedName("paymentDescription")
    @Expose
    var paymentDescription: String,

    @SerializedName("transactionHash")
    @Expose
    var transactionHash: String

)