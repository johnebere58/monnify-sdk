package com.monnify.monnify_payment_sdk.data.model

enum class CardChargeStatus {
    PENDING,
    SUCCESS,
    OTP_AUTHORIZATION_REQUIRED,
    BANK_AUTHORIZATION_REQUIRED,
    MPGS_3DS_AUTHORIZATION_REQUIRED,
    AUTHENTICATION_FAILED,
    PIN_REQUIRED,
    FAILED;
}