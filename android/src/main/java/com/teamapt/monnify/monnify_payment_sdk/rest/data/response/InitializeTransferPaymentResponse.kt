package com.teamapt.monnify.monnify_payment_sdk.rest.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class InitializeTransferPaymentResponse(
    @SerializedName("accountNumber")
    @Expose
    var accountNumber: String? = null,

    @SerializedName("accountName")
    @Expose
    var accountName: String? = null,

    @SerializedName("bankName")
    @Expose
    var bankName: String? = null,

    @SerializedName("accountDurationSeconds")
    @Expose
    var accountDurationSeconds: Int? = null,

    @SerializedName("ussdPayment")
    @Expose
    var ussdPayment: String? = null,

    @SerializedName("requestTime")
    @Expose
    var requestTime: String? = null,

    @SerializedName("transactionReference")
    @Expose
    var transactionReference: String? = null,

    @SerializedName("paymentReference")
    @Expose
    var paymentReference: String? = null,

    @SerializedName("amount")
    @Expose
    var amount: BigDecimal = BigDecimal.ZERO,

    @SerializedName("fee")
    @Expose
    var fee: BigDecimal = BigDecimal.ZERO,

    @SerializedName("totalPayable")
    @Expose
    var totalPayable: BigDecimal = BigDecimal.ZERO
)