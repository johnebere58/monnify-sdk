package com.teamapt.monnify.sdk.rest.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Secure3dData(
    @SerializedName("callBackUrl")
    @Expose
    var callBackUrl: String?,

    @SerializedName("id")
    @Expose
    var id: String?,

    @SerializedName("method")
    @Expose
    var method: String?,

    @SerializedName("redirectUrl")
    @Expose
    var redirectUrl: String?,

    @SerializedName("termUrl")
    @Expose
    var termUrl: String?,

    @SerializedName("acsUrl")
    @Expose
    var acsUrl: String?,

    @SerializedName("eciFlag")
    @Expose
    var eciFlag: String?,

    @SerializedName("paReq")
    @Expose
    var paReq: String?,

    @SerializedName("transactionId")
    @Expose
    var transactionId: String?,

    @SerializedName("paymentId")
    @Expose
    var paymentId: String?,

    @SerializedName("md")
    @Expose
    var md: String?
)