package com.monnify.monnify_payment_sdk.rest.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MonnifyApiResponse<T>(
    @SerializedName("requestSuccessful")
    @Expose
    var requestSuccessful: Boolean?,

    @SerializedName("responseMessage")
    @Expose
    var responseMessage: String?,

    @SerializedName("responseCode")
    @Expose
    var responseCode: String?,

    @SerializedName("responseBody")
    @Expose
    var responseBody: T?
) {

    fun isSuccessful(): Boolean {
        return requestSuccessful == true && responseMessage?.lowercase() == "success"
    }
}