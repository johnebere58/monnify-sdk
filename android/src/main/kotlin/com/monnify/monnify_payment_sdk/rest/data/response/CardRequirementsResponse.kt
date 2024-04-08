package com.monnify.monnify_payment_sdk.rest.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CardRequirementsResponse(
    @SerializedName("requirePin")
    @Expose
    var requirePin: Boolean = false
)
