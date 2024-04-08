package com.teamapt.monnify.sdk.rest.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CardRequirementsResponse(
    @SerializedName("requirePin")
    @Expose
    var requirePin: Boolean = false
)
