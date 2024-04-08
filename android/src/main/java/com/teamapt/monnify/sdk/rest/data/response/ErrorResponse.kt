package com.teamapt.monnify.sdk.rest.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("requestSuccessful")
    @Expose
    var requestSuccessful: Boolean? = null,

    @SerializedName("responseMessage")
    @Expose
    var responseMessage: String?,

    @SerializedName("responseCode")
    @Expose
    var responseCode: String?
)