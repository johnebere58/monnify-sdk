package com.teamapt.monnify.sdk.module

import com.teamapt.monnify.sdk.service.ApplicationMode

interface LocalMerchantKeyProvider {
    fun getApiKey(): String?
    fun getContractCode(): String?
    fun getApplicationMode(): ApplicationMode?
}
