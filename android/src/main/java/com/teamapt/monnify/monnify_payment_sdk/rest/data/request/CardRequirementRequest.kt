package com.teamapt.monnify.monnify_payment_sdk.rest.data.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CardRequirementRequest(
    @SerializedName("pan")
    @Expose
    var pan: String? = null
)