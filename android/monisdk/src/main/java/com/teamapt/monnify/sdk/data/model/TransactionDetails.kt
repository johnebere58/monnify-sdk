package com.teamapt.monnify.sdk.data.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.teamapt.monnify.sdk.rest.data.request.SubAccountDetails
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class TransactionDetails(
    @SerializedName("amount")
    @Expose
    var amount: BigDecimal,

    @SerializedName("customerName")
    @Expose
    var customerName: String,

    @SerializedName("customerEmail")
    @Expose
    var customerEmail: String,

    @SerializedName("paymentReference")
    @Expose
    var paymentReference: String,

    @SerializedName("paymentDescription")
    @Expose
    var paymentDescription: String,

    @SerializedName("currencyCode")
    @Expose
    var currencyCode: String,

    @SerializedName("metaData")
    @Expose
    var metaData: Map<String, String>? = null,

    @SerializedName("paymentMethods")
    @Expose
    var paymentMethods: List<com.teamapt.monnify.sdk.model.PaymentMethod>? = null,

    @SerializedName("incomeSplitConfig")
    @Expose
    var incomeSplitConfig: List<SubAccountDetails>? = ArrayList()

) : Parcelable {

    class Builder {
        private var amount: BigDecimal = BigDecimal("0.0")
        private var customerName: String = ""
        private var customerEmail: String = ""
        private var paymentReference: String = ""
        private var paymentDescription: String = ""
        private var currencyCode: String = ""
        private var metaData: Map<String, String>? = hashMapOf()
        private var paymentMethods: List<com.teamapt.monnify.sdk.model.PaymentMethod>? = arrayListOf()
        private var incomeSplitConfig: List<SubAccountDetails>? = arrayListOf()

        fun amount(amount: BigDecimal) = apply { this.amount = amount }
        fun customerName(customerName: String) = apply { this.customerName = customerName }
        fun customerEmail(customerEmail: String) = apply { this.customerEmail = customerEmail }
        fun paymentReference(paymentReference: String) =
            apply { this.paymentReference = paymentReference }

        fun paymentDescription(paymentDescription: String) =
            apply { this.paymentDescription = paymentDescription }

        fun currencyCode(currencyCode: String) = apply { this.currencyCode = currencyCode }

        fun metaData(metaData: Map<String, String>) =
            apply { this.metaData = metaData }

        fun paymentMethods(paymentMethod: List<com.teamapt.monnify.sdk.model.PaymentMethod>) =
            apply { this.paymentMethods = paymentMethod }

        fun incomeSplitConfig(incomeSplitConfig: List<SubAccountDetails>) =
            apply { this.incomeSplitConfig = incomeSplitConfig }

        fun build() = TransactionDetails(
            amount,
            customerName,
            customerEmail,
            paymentReference,
            paymentDescription,
            currencyCode,
            metaData,
            paymentMethods,
            incomeSplitConfig
        )
    }
}
