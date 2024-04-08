package com.teamapt.monnify.monnify_payment_sdk.rest.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TokenData(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("message")
    @Expose
    var message: String? = null
)