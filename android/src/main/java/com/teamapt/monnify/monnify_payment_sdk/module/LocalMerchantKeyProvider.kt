package com.teamapt.monnify.monnify_payment_sdk.module

import com.teamapt.monnify.monnify_payment_sdk.service.ApplicationMode

interface LocalMerchantKeyProvider {
    fun getApiKey(): String?
    fun getContractCode(): String?
    fun getApplicationMode(): ApplicationMode?
}
