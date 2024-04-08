package com.teamapt.monnify.sdk.rest.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class CardPaymentResponse(
    @SerializedName("status")
    @Expose
    var status: String?,
    @SerializedName("message")
    @Expose
    var message: String?,
    @SerializedName("cardToken")
    @Expose
    var cardToken: String?,
    @SerializedName("otpData")
    @Expose
    var tokenData: TokenData?,
    @SerializedName("secure3dData")
    @Expose
    var secure3dData: Secure3dData?,
    @SerializedName("paymentReference")
    @Expose
    var paymentReference: String?,
    @SerializedName("responseDescription")
    @Expose
    var responseDescription: String?,
    @SerializedName("authorizedAmount")
    @Expose
    var authorizedAmount: BigDecimal? = BigDecimal.ZERO,
    @SerializedName("transactionReference")
    @Expose
    var transactionReference: String?
)