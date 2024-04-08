package com.teamapt.monnify.monnify_payment_sdk.rest.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Bank(
    @SerializedName("name")
    @Expose
    var name: String? = null,
    @SerializedName("code")
    @Expose
    var code: String? = null,
    @SerializedName("ussdTemplate")
    @Expose
    var ussdTemplate: String? = null,
    @SerializedName("baseUssdCode")
    @Expose
    var baseUssdCode: String? = null,
    @SerializedName("transferUssdTemplate")
    @Expose
    var transferUssdTemplate: String? = null
) {

    override fun toString(): String {
        return name!!
    }
}