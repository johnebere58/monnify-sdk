package com.teamapt.monnify.monnify_payment_sdk

import android.os.Parcelable
import com.teamapt.monnify.monnify_payment_sdk.data.model.PaymentMethod
import com.teamapt.monnify.monnify_payment_sdk.data.model.PaymentStatus
import com.teamapt.monnify.monnify_payment_sdk.data.model.TransactionType
import com.teamapt.monnify.monnify_payment_sdk.rest.data.response.TransactionStatusResponse
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class MonnifyTransactionResponse(
    var paymentReference: String = "",

    var transactionReference: String = "",

    var paymentDescription: String = "",

    var customerName: String = "",

    var customerEmail: String = "",

    var status: Status = Status.CANCELLED,

    var currencyCode: String = "",

    var amountPayable: BigDecimal = BigDecimal.ZERO,

    var amountPaid: BigDecimal = BigDecimal.ZERO,

    var paymentMethod: TransactionType? = null,

    var paidOn: String? = null,

    var message: String? = null

) : Parcelable {

    companion object {

        fun fromTransactionResponse(
            response: MonnifyTransactionResponse?,
            transactionResponse: TransactionStatusResponse
        ): MonnifyTransactionResponse {

            val result = response ?: MonnifyTransactionResponse()

            result.paymentReference = transactionResponse.paymentReference ?: ""
            result.transactionReference = transactionResponse.transactionReference ?: ""
            result.paymentDescription = transactionResponse.paymentDescription ?: ""
            result.customerName = transactionResponse.customerName ?: ""
            result.customerEmail = transactionResponse.customerEmail ?: ""
            result.status = Status.get(
                PaymentStatus.valueOf(
                    transactionResponse.paymentStatus
                        ?: PaymentStatus.PENDING.name
                )
            )
            result.currencyCode = transactionResponse.currencyCode ?: ""
            result.amountPaid = transactionResponse.amount ?: BigDecimal.ZERO
            result.amountPayable = transactionResponse.payableAmount ?: BigDecimal.ZERO

            val paymentMethod = PaymentMethod.valueOf(transactionResponse.paymentMethod!!)

            response?.paymentMethod = when (paymentMethod) {
                PaymentMethod.ACCOUNT_TRANSFER -> TransactionType.BANK_TRANSFER
                PaymentMethod.CARD -> TransactionType.CARD
                PaymentMethod.DIRECT_DEBIT -> TransactionType.DIRECT_DEBIT
                PaymentMethod.PHONE_NUMBER -> TransactionType.PHONE_NUMBER
                PaymentMethod.USSD -> TransactionType.USSD
                else -> TODO("Added other payment method")
            }

            result.paidOn = transactionResponse.completedOn ?: ""

            return result
        }
    }

}