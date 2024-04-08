package com.teamapt.monnify.sdk.service

import com.teamapt.monnify.sdk.rest.ApiUtils
import com.teamapt.monnify.sdk.rest.api.MonnifyAPIClient
import com.teamapt.monnify.sdk.rest.data.request.*
import com.teamapt.monnify.sdk.rest.data.response.*
import io.reactivex.Single

class RestService(environment: Environment) {
    private var monnifyAPIClient: MonnifyAPIClient

    init {
        if (restServiceInstance != null) {
            throw RuntimeException("Use getInstance() method to get the single instance of this class.")
        }
        monnifyAPIClient = ApiUtils.createFrontOfficeAPI(environment)
    }

    fun initializeTransaction(initializeTransactionRequest: InitializeTransactionRequest): Single<MonnifyApiResponse<InitializeTransactionResponse>> {
        return monnifyAPIClient
            .initializeTransaction(initializeTransactionRequest)
    }

    fun initializeBankPayment(initializeBankPaymentRequest: InitializeBankPaymentRequest): Single<MonnifyApiResponse<InitializeBankPaymentResponse>> {
        return monnifyAPIClient.initializeBankPayment(initializeBankPaymentRequest)
    }

    fun getBankPaymentBankList(): Single<MonnifyApiResponse<List<Bank>>> {
        return monnifyAPIClient.getBankPaymentBankList()
    }

    fun bankPaymentVerifyAccountNumber(bankPaymentVerifyRequest: BankPaymentVerifyRequest): Single<MonnifyApiResponse<BankPaymentVerifyResponse>> {
        return monnifyAPIClient.bankPaymentVerifyAccountNumber(bankPaymentVerifyRequest)
    }

    fun bankPaymentChargeAccount(bankPaymentChargeRequest: BankPaymentChargeRequest): Single<MonnifyApiResponse<BankPaymentChargeResponse>> {
        return monnifyAPIClient.bankPaymentChargeAccount(bankPaymentChargeRequest)
    }

    fun bankPaymentAuthorizePayment(bankPaymentAuthorizeRequest: BankPaymentAuthorizeRequest): Single<MonnifyApiResponse<BankPaymentAuthorizeResponse>> {
        return monnifyAPIClient.bankPaymentAuthorizePayment(bankPaymentAuthorizeRequest)
    }

    fun initializeTransferPayment(initializeTransferPaymentRequest: InitializeTransferPaymentRequest): Single<MonnifyApiResponse<InitializeTransferPaymentResponse>> {
        return monnifyAPIClient
            .initializeTransferPayment(initializeTransferPaymentRequest)
    }

    fun initializeUssdPayment(initializeUssdPaymentRequest: InitializeUssdPaymentRequest): Single<MonnifyApiResponse<InitializeUssdPaymentResponse>> {
        return monnifyAPIClient.initializeUssdPayment(initializeUssdPaymentRequest)
    }

    fun initializePhonePayment(initializePhonePaymentRequest: InitializePhonePaymentRequest): Single<MonnifyApiResponse<InitializePhonePaymentResponse>>
    {
        return monnifyAPIClient.initializePhonePayment(initializePhonePaymentRequest)
    }

    fun initializeCardPayment(initializeCardPaymentRequest: InitializeCardPaymentRequest): Single<MonnifyApiResponse<InitializeCardPaymentResponse>> {
        return monnifyAPIClient
            .initializeCardPayment(initializeCardPaymentRequest)
    }

    fun payWithCard(cardPayRequest: CardPaymentRequest): Single<MonnifyApiResponse<CardPaymentResponse>> {
        return monnifyAPIClient
            .payWithCard(cardPayRequest)
    }

    fun authorizeCardOtp(authorizeCardOtpRequest: AuthorizeCardOtpRequest): Single<MonnifyApiResponse<CardPaymentResponse>> {
        return monnifyAPIClient
            .authorizeCardOtp(authorizeCardOtpRequest)
    }

    fun authorizeCardSecure3D(authorizeCardSecure3DRequest: AuthorizeCardSecure3DRequest): Single<MonnifyApiResponse<CardPaymentResponse>> {
        return monnifyAPIClient
            .authorizeCardOtp(authorizeCardSecure3DRequest)
    }

    fun getCardRequirements(cardRequirementRequest: CardRequirementRequest):
            Single<MonnifyApiResponse<CardRequirementsResponse>> {
        return monnifyAPIClient
            .getCardRequirements(cardRequirementRequest)
    }

    fun checkTransactionStatus(
        apiKey: String?,
        transactionReference: String
    ): Single<MonnifyApiResponse<TransactionStatusResponse>> {
        return monnifyAPIClient
            .checkTransactionStatus(apiKey, transactionReference)
    }

    fun getAllBanks(): Single<MonnifyApiResponse<List<Bank>>> {
        return monnifyAPIClient.getAllBanks()
    }

    companion object {
        @Volatile
        private var restServiceInstance: RestService? = null

        fun getInstance(environment: Environment): RestService? {
            if (restServiceInstance == null) {
                synchronized(RestService::class.java) {
                    if (restServiceInstance == null)
                        restServiceInstance = RestService(environment)
                }
            }
            return restServiceInstance

        }

        fun destroySingleton() {
            restServiceInstance = null
        }

    }

}
