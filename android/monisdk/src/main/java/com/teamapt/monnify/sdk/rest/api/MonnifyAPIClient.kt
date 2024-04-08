package com.teamapt.monnify.sdk.rest.api

import com.teamapt.monnify.sdk.rest.Urls
import com.teamapt.monnify.sdk.rest.annotation.SpecificTimeout
import com.teamapt.monnify.sdk.rest.data.request.*
import com.teamapt.monnify.sdk.rest.data.response.*
import io.reactivex.Single
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface MonnifyAPIClient {

    @POST(Urls.EP_INITIALIZE_TRANSACTION)
    fun initializeTransaction(@Body initializeTransactionRequest: InitializeTransactionRequest):
            Single<MonnifyApiResponse<InitializeTransactionResponse>>

    @POST(Urls.EP_INIT_TRANSFER_PAYMENT)
    fun initializeTransferPayment(@Body initializeTransferPaymentRequest: InitializeTransferPaymentRequest):
            Single<MonnifyApiResponse<InitializeTransferPaymentResponse>>

    @POST(Urls.EP_INIT_CARD_PAYMENT)
    fun initializeCardPayment(@Body initializeCardPaymentRequest: InitializeCardPaymentRequest):
            Single<MonnifyApiResponse<InitializeCardPaymentResponse>>

    @POST(Urls.EP_PAY_CARD)
    fun payWithCard(@Body cardPaymentRequest: CardPaymentRequest):
            Single<MonnifyApiResponse<CardPaymentResponse>>

    @POST(Urls.EP_PAY_CARD_OTP)
    fun payWithCardOtp(@Body cardPaymentRequest: CardPaymentRequest):
            Single<MonnifyApiResponse<CardPaymentResponse>>

    @POST(Urls.EP_AUTHORIZE_CARD_OTP)
    fun authorizeCardOtp(@Body authorizeCardOtpRequest: AuthorizeCardOtpRequest):
            Single<MonnifyApiResponse<CardPaymentResponse>>

    @POST(Urls.EP_AUTHORIZE_CARD_SECURE_3D)
    fun authorizeCardOtp(@Body authorizeCardSecure3DRequest: AuthorizeCardSecure3DRequest):
            Single<MonnifyApiResponse<CardPaymentResponse>>

    @POST(Urls.EP_CARD_REQUIREMENTS)
    fun getCardRequirements(@Body cardRequirementRequest: CardRequirementRequest):
            Single<MonnifyApiResponse<CardRequirementsResponse>>

    @GET(Urls.EP_CHECK_TRANSACTION_STATUS + "/{apiKey}?")
    fun checkTransactionStatus(
        @Path("apiKey") apiKey: String?,
        @Query("transactionReference") transactionReference: String
    ):
            Single<MonnifyApiResponse<TransactionStatusResponse>>

    @GET(Urls.EP_GET_ALL_BANKS)
    fun getAllBanks():
            Single<MonnifyApiResponse<List<Bank>>>

    @POST(Urls.EP_INIT_USSD_PAYMENT)
    fun initializeUssdPayment(@Body initializeUssdPaymentRequest: InitializeUssdPaymentRequest):
            Single<MonnifyApiResponse<InitializeUssdPaymentResponse>>

    @POST(Urls.EP_INIT_PHONE_PAYMENT)
    @SpecificTimeout(300000, TimeUnit.MILLISECONDS)
    fun initializePhonePayment(@Body initializePhonePaymentRequest: InitializePhonePaymentRequest):
            Single<MonnifyApiResponse<InitializePhonePaymentResponse>>

    @POST(Urls.EP_INIT_BANK_PAYMENT)
    fun initializeBankPayment(@Body initializeBankPaymentRequest: InitializeBankPaymentRequest):
            Single<MonnifyApiResponse<InitializeBankPaymentResponse>>

    @GET(Urls.EP_BANK_GET_BANK_LIST)
    fun getBankPaymentBankList(): Single<MonnifyApiResponse<List<Bank>>>

    @POST(Urls.EP_BANK_VERIFY)
    fun bankPaymentVerifyAccountNumber(@Body bankPaymentVerifyRequest: BankPaymentVerifyRequest):
            Single<MonnifyApiResponse<BankPaymentVerifyResponse>>

    @POST(Urls.EP_BANK_CHARGE)
    fun bankPaymentChargeAccount(@Body bankPaymentChargeRequest: BankPaymentChargeRequest):
            Single<MonnifyApiResponse<BankPaymentChargeResponse>>

    @POST(Urls.EP_BANK_AUTHORIZE_OTP)
    fun bankPaymentAuthorizePayment(@Body bankPaymentAuthorizeRequest: BankPaymentAuthorizeRequest):
            Single<MonnifyApiResponse<BankPaymentAuthorizeResponse>>
}
