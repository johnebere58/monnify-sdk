package com.teamapt.monnify.sdk.util

interface Constants {

    companion object {

        const val TRANSACTION_DETAILS_ARGS = "payment_particulars"
        const val ARG_TRANSACTION_DETAILS = "transaction_details"
        const val ARG_BANKS_PROVIDER = "banks_provider"

        const val COLLECTION_CHANNEL = "MOBILE_SDK"
        const val ALT_COLLECTION_CHANNEL = "API_NOTIFICATION"

        const val ARG_TRANSACTION_REF = "TRANSACTION REFERENCE"
        const val ARG_ERROR_TEXT = "ARG_ERROR_TEXT"
        const val ARG_TRANSACTION_STATUS_DETAILS = "ARG_TRANSACTION_STATUS_DETAILS"
        const val ARG_USE_SAVED_CARD = "ARG_USE_SAVED_CARD"

        const val PREFERENCES_FILE = "pref_file_blably_not_to_clash_with_merchants"
        const val PREF_BANKS = "all_banks"
        const val PREF_BANK_PAYMENT_BANKS = "bank_payment_banks"
        const val PREF_LAST_BANK_LOAD_TIME = "time_banks_last_loaded"
        const val PREF_LAST_BANK_PAYMENT_BANK_LOAD_TIME = "time_bank_payment_banks_last_loaded"
        const val PREF_APPLICATION_MODE = "pref_application_mode"
        const val PREF_API_KEY = "pref_api_key"
        const val PREF_CONTRACT_CODE = "pref_contract_code"

        const val DRAWABLE_LEFT = 1
        const val DRAWABLE_RIGHT = 2
        const val DRAWABLE_TOP = 3
        const val DRAWABLE_BOTTOM = 4
    }
}
