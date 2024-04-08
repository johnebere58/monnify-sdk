package com.teamapt.monnify.sdk.rest.data.request

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.math.BigDecimal

@Parcelize
data class SubAccountDetails(
    @SerializedName("subAccountCode")
    @Expose
    var subAccountCode: String? = null,
    @SerializedName("feePercentage")
    @Expose
    var feePercentage: Float? = null,
    @SerializedName("splitAmount")
    @Expose
    var splitAmount: BigDecimal? = null,
    @SerializedName("feeBearer")
    @Expose
    var feeBearer: Boolean? = null

) : Serializable, Parcelable