package com.teamapt.monnify.sdk.rest.data.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CardRequirementRequest(
    @SerializedName("pan")
    @Expose
    var pan: String? = null
)