package com.teamapt.monnify.sdk

import com.teamapt.monnify.sdk.data.model.CardChargeStatus
import com.teamapt.monnify.sdk.data.model.PaymentStatus

enum class Status {
    PAID,
    OVERPAID,
    PARTIALLY_PAID,
    FAILED,
    PENDING,
    CANCELLED,
    PAYMENT_GATEWAY_ERROR;

    companion object {

        fun get(cardChargeStatus: CardChargeStatus): Status =
            when (cardChargeStatus) {
                CardChargeStatus.SUCCESS -> PAID
                CardChargeStatus.FAILED -> FAILED
                else -> PENDING
            }

        fun get(notifiedTransactionStatus: PaymentStatus): Status =
            when (notifiedTransactionStatus) {
                PaymentStatus.PAID -> PAID
                PaymentStatus.PARTIALLY_PAID -> PARTIALLY_PAID
                PaymentStatus.OVERPAID -> OVERPAID
                PaymentStatus.EXPIRED -> FAILED
                PaymentStatus.CANCELLED -> CANCELLED
                PaymentStatus.REVERSED -> FAILED
                PaymentStatus.FAILED -> FAILED
                PaymentStatus.PENDING -> PENDING
            }

    }
}