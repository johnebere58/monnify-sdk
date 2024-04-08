package com.teamapt.monnify.sdk.rest.data.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.teamapt.monnify.sdk.data.model.PaymentMethod
import com.teamapt.monnify.sdk.data.model.TransactionDetails
import com.teamapt.monnify.sdk.util.Constants
import java.math.BigDecimal

data class InitializeTransactionRequest(
    @SerializedName("amount")
    @Expose
    var amount: BigDecimal,

    @SerializedName("customerName")
    @Expose
    var customerName: String,

    @SerializedName("customerEmail")
    @Expose
    var customerEmail: String,

    @SerializedName("paymentReference")
    @Expose
    var paymentReference: String,

    @SerializedName("paymentDescription")
    @Expose
    var paymentDescription: String,

    @SerializedName("currencyCode")
    @Expose
    var currencyCode: String,

    @SerializedName("apiKey")
    @Expose
    var apiKey: String? = null,

    @SerializedName("contractCode")
    @Expose
    var contractCode: String? = null,

    @SerializedName("metaData")
    @Expose
    var metaData: Map<String, String>? = null,

    @SerializedName("paymentMethods")
    @Expose
    var paymentMethods: List<PaymentMethod>? = null,

    @SerializedName("collectionChannel")
    @Expose
    var collectionChannel: String = Constants.COLLECTION_CHANNEL,

    @SerializedName("incomeSplitConfig")
    @Expose
    var incomeSplitConfig: List<SubAccountDetails>? = null
) {

    constructor(
        transactionDetails: TransactionDetails,
        apiKey: String? = null,
        contractCode: String? = null
    ) : this(
        amount = transactionDetails.amount,
        customerName = transactionDetails.customerName,
        customerEmail = transactionDetails.customerEmail,
        paymentReference = transactionDetails.paymentReference,
        paymentDescription = transactionDetails.paymentDescription,
        currencyCode = transactionDetails.currencyCode,
        apiKey = apiKey,
        contractCode = contractCode,
        metaData = transactionDetails.metaData,
        paymentMethods = transactionDetails.paymentMethods?.map { PaymentMethod.valueOf(it.toString()) },
        incomeSplitConfig = transactionDetails.incomeSplitConfig
    )

}
