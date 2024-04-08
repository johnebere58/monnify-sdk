package com.monnify.monnify_payment_sdk.rest.data.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.monnify.monnify_payment_sdk.util.Constants

data class BankPaymentChargeRequest(

    @SerializedName("transactionReference")
    @Expose
    val transactionReference: String?,

    @SerializedName("bankCode")
    @Expose
    val bankCode: String?,

    @SerializedName("accountNumber")
    @Expose
    val accountNumber: String?,

    @SerializedName("dob")
    @Expose
    val dob: String?,

    @SerializedName("bvn")
    @Expose
    val bvn: String?,

    @SerializedName("apiKey")
    @Expose
    val apiKey: String?,

    @SerializedName("collectionChannel")
    @Expose
    val collectionChannel: String = Constants.COLLECTION_CHANNEL
)