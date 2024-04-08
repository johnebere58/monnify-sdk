package com.teamapt.monnify.sdk.rest.data.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Card(
    @SerializedName("number")
    @Expose
    var number: String? = null,

    @SerializedName("expiryMonth")
    @Expose
    var expiryMonth: Int? = null,

    @SerializedName("expiryYear")
    @Expose
    var expiryYear: Int? = null,

    @SerializedName("cvv")
    @Expose
    var cvv: String? = null,

    @SerializedName("pin")
    @Expose
    var pin: String? = null
)
