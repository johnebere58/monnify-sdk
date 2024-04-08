package com.teamapt.monnify.monnify_payment_sdk.data.model

import android.os.Parcel
import android.os.Parcelable
import com.teamapt.monnify.monnify_payment_sdk.Status
import java.math.BigDecimal

data class TransactionStatusDetails(
    var isCompleted: Boolean = false,
    var status: Status = Status.PENDING,
    var message: String? = "",
    var amountPaid: BigDecimal = BigDecimal.ZERO,
    var amountPayable: BigDecimal = BigDecimal.ZERO,
    var paymentReference: String = "",
    var transactionReference: String = "",
    var currencyCode: String? = "",
    val paymentMethod: String? = ""
) : Parcelable {

    class Builder {
        private var isCompleted: Boolean = false
        private var status: Status = Status.PENDING
        private var message: String? = ""
        private var amountPaid: BigDecimal = BigDecimal.ZERO
        private var amountPayable: BigDecimal = BigDecimal.ZERO
        private var paymentReference: String = ""
        private var transactionReference: String = ""
        private var currencyCode: String? = ""
        private var paymentMethod: String? = ""

        fun isCompleted(isCompleted: Boolean) = apply { this.isCompleted = isCompleted }
        fun status(status: Status) = apply { this.status = status }
        fun message(message: String?) = apply { this.message = message }
        fun amountPaid(amountPaid: BigDecimal?) =
            apply { this.amountPaid = amountPaid ?: BigDecimal.ZERO }

        fun amountPayable(amountPayable: BigDecimal?) =
            apply { this.amountPayable = amountPayable ?: BigDecimal.ZERO }

        fun paymentReference(paymentReference: String) =
            apply { this.paymentReference = paymentReference }

        fun transactionReference(transactionReference: String) =
            apply { this.transactionReference = transactionReference }

        fun paymentMethod(paymentMethod: String?) =
            apply { this.paymentMethod = paymentMethod }

        fun currencyCode(currencyCode: String?) = apply { this.currencyCode = currencyCode }
        fun build() = TransactionStatusDetails(
            isCompleted,
            status,
            message,
            amountPaid,
            amountPayable,
            paymentReference,
            transactionReference,
            currencyCode,
            paymentMethod
        )
    }

    constructor(source: Parcel) : this(
        1 == source.readInt(),
        source.readValue(Int::class.java.classLoader).let { Status.values()[it as Int] },
        source.readString() as String,
        source.readSerializable() as BigDecimal,
        source.readSerializable() as BigDecimal,
        source.readString() as String,
        source.readString() as String,
        source.readString() as String,
        source.readString() as String
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt((if (isCompleted) 1 else 0))
        writeValue(status.ordinal)
        writeString(message)
        writeSerializable(amountPaid)
        writeSerializable(amountPayable)
        writeString(paymentReference)
        writeString(transactionReference)
        writeString(currencyCode)
        writeString(paymentMethod)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TransactionStatusDetails> =
            object : Parcelable.Creator<TransactionStatusDetails> {
                override fun createFromParcel(source: Parcel): TransactionStatusDetails =
                    TransactionStatusDetails(source)

                override fun newArray(size: Int): Array<TransactionStatusDetails?> =
                    arrayOfNulls(size)
            }
    }
}
