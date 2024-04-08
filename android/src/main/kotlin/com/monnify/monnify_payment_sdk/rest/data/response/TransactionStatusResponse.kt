package com.monnify.monnify_payment_sdk.rest.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class TransactionStatusResponse(

    @SerializedName("paymentMethod")
    @Expose
    var paymentMethod: String?,
    @SerializedName("createdOn")
    @Expose
    var createdOn: String?,
    @SerializedName("amount")
    @Expose
    var amount: BigDecimal?,
    @SerializedName("fee")
    @Expose
    var fee: BigDecimal?,
    @SerializedName("currencyCode")
    @Expose
    var currencyCode: String?,
    @SerializedName("completedOn")
    @Expose
    var completedOn: String?,
    @SerializedName("customerName")
    @Expose
    var customerName: String?,
    @SerializedName("customerEmail")
    @Expose
    var customerEmail: String?,
    @SerializedName("paymentDescription")
    @Expose
    var paymentDescription: String?,
    @SerializedName("paymentStatus")
    @Expose
    var paymentStatus: String? ,
    @SerializedName("transactionReference")
    @Expose
    var transactionReference: String?,
    @SerializedName("paymentReference")
    @Expose
    var paymentReference: String?,
    @SerializedName("payableAmount")
    @Expose
    var payableAmount: BigDecimal?,
    @SerializedName("amountPaid")
    @Expose
    var amountPaid: BigDecimal?,
    @SerializedName("completed")
    @Expose
    var completed: Boolean?


)