package com.teamapt.monnify.sdk.rest

interface Urls {
    companion object {

        private const val BASE_API_V1 = "api/v1/sdk/"

        const val EP_INITIALIZE_TRANSACTION = BASE_API_V1 + "transactions/init-transaction"
        const val EP_INIT_TRANSFER_PAYMENT = BASE_API_V1 + "bank-transfer/init-payment"
        const val EP_INIT_USSD_PAYMENT = BASE_API_V1 + "ussd/initialize"
        const val EP_INIT_CARD_PAYMENT = BASE_API_V1 + "cards/init-card-transaction"
        const val EP_INIT_PHONE_PAYMENT = BASE_API_V1 + "phone-payment/initialize"
        const val EP_INIT_BANK_PAYMENT = BASE_API_V1 + "bank/init-transaction"
        const val EP_PAY_CARD = BASE_API_V1 + "cards/charge"
        const val EP_PAY_CARD_OTP = BASE_API_V1 + "cards/charge"
        const val EP_AUTHORIZE_CARD_OTP = BASE_API_V1 + "cards/otp/authorize"
        const val EP_AUTHORIZE_CARD_SECURE_3D = BASE_API_V1 + "cards/secure-3d/authorize"
        const val EP_CARD_REQUIREMENTS = BASE_API_V1 + "cards/requirements"
        const val EP_GET_ALL_BANKS = BASE_API_V1 + "transactions/banks"
        const val EP_CHECK_TRANSACTION_STATUS = BASE_API_V1 + "transactions/query"
        const val EP_BANK_VERIFY = BASE_API_V1 + "bank/verify"
        const val EP_BANK_CHARGE = BASE_API_V1 + "bank/charge"
        const val EP_BANK_AUTHORIZE_OTP = BASE_API_V1 + "bank/authorize/otp"
        const val EP_BANK_GET_BANK_LIST = BASE_API_V1 + "bank/list-banks"
    }
}
