package com.teamapt.monnify.monnify_payment_sdk.module.vm

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.monnify.monnify_payment_sdk.R
import com.teamapt.monnify.monnify_payment_sdk.data.Event
import com.teamapt.monnify.monnify_payment_sdk.data.SingleLiveEvent
import com.teamapt.monnify.monnify_payment_sdk.data.model.PaymentMethod
import com.teamapt.monnify.monnify_payment_sdk.rest.data.request.BankPaymentAuthorizeRequest
import com.teamapt.monnify.monnify_payment_sdk.rest.data.request.BankPaymentChargeRequest
import com.teamapt.monnify.monnify_payment_sdk.rest.data.request.BankPaymentVerifyRequest
import com.teamapt.monnify.monnify_payment_sdk.rest.data.request.InitializeBankPaymentRequest
import com.teamapt.monnify.monnify_payment_sdk.rest.data.response.*
import com.teamapt.monnify.monnify_payment_sdk.util.BanksProvider
import com.teamapt.monnify.monnify_payment_sdk.util.Constants
import com.teamapt.monnify.monnify_payment_sdk.util.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class BankPaymentViewModel(private val banksProvider: BanksProvider) : BaseActiveViewModel() {

    companion object {
        enum class BankPaymentState {
            VERIFY,
            VALIDATE,
            CONFIRM
        }
    }

    private lateinit var initializeBankPaymentRequest: InitializeBankPaymentRequest
    private lateinit var initializeBankPaymentResponse: InitializeBankPaymentResponse

    private lateinit var bankPaymentVerifyRequest: BankPaymentVerifyRequest
    private lateinit var bankPaymentVerifyResponse: BankPaymentVerifyResponse

    private lateinit var bankPaymentChargeRequest: BankPaymentChargeRequest
    private lateinit var bankPaymentChargeResponse: BankPaymentChargeResponse

    private lateinit var bankPaymentAuthorizeRequest: BankPaymentAuthorizeRequest
    private lateinit var bankPaymentAuthorizeResponse: BankPaymentAuthorizeResponse

    private var selectedBank: Bank? = null

    private var accountNumber: String = ""
    private var bvn: String = ""
    private var dateOfBirth = ""
    private var token = ""

    val liveGetBankPaymentBanksResponse = MutableLiveData<List<Bank>>()

    val liveInitializeBankPaymentResponse = MutableLiveData<Event<InitializeBankPaymentResponse>>()
    val liveBankPaymentVerifyAccount = SingleLiveEvent<BankPaymentVerifyResponse>()
    val liveBankPaymentChargeAccount = MutableLiveData<BankPaymentChargeResponse>()

    var liveSetBankPaymentState = SingleLiveEvent<BankPaymentState>()

    val liveEventOnBankSelected = SingleLiveEvent<Bank>()

    var liveEventActivateContinueButton = SingleLiveEvent<Boolean>()
    var liveEventActivateOtpAuthorizeButton = SingleLiveEvent<Boolean>()
    var liveEventSetContinueButtonLoading = SingleLiveEvent<Boolean>()

    override fun init(bundle: Bundle) {
        Logger.log(this, "init was called in ${this.javaClass.simpleName}")

        liveSetBankPaymentState.value = BankPaymentState.VERIFY
        loadAllBanksMakeApiCallIfRequired()
    }

    fun resetState() {
        setBankPaymentState(BankPaymentState.VERIFY)

        liveEventActivateContinueButton.postValue(false)

        accountNumber = ""
        bvn = ""
        dateOfBirth = ""
        token = ""
    }

    fun loadBanks() {
        loadAllBanksMakeApiCallIfRequired()
    }

    fun setSelectedBank(value: Bank) {
        this.selectedBank = value
        liveEventOnBankSelected.value = value
    }

    fun getSelectedBank(): Bank? {
        return selectedBank
    }

    fun initializeBankPayment() {
        initializeBankPaymentRequest = InitializeBankPaymentRequest(
            transactionReference = transactionReference,
            apiKey = localMerchantKeyProvider.getApiKey(),
            collectionChannel = Constants.COLLECTION_CHANNEL
        )

        Logger.log(this, "initializeBankPayment was called")

        commonUIFunctions.showLoading(messageResId = R.string.initializing_transaction)

        disposables.add(restService.initializeBankPayment(initializeBankPaymentRequest)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {}
            .subscribe(
                {
                    commonUIFunctions.dismissLoading()
                    handleInitializeBankTransferResponse(it)
                },
                {
                    commonUIFunctions.dismissLoading()
                    this.handleError(it)                }
            )
        )
    }

    private fun handleInitializeBankTransferResponse(initializeBankPaymentApiResponse: MonnifyApiResponse<InitializeBankPaymentResponse>?) {


        Logger.log(this, "handleInitializeBankTransferResponse was called")

        if (initializeBankPaymentApiResponse != null && initializeBankPaymentApiResponse.isSuccessful()) {

            initializeBankPaymentResponse = initializeBankPaymentApiResponse.responseBody!!
            liveInitializeBankPaymentResponse.value = Event(initializeBankPaymentResponse)
        }
    }

    private fun loadAllBanksMakeApiCallIfRequired() {

        if (banksProvider.bankPaymentBanksNotLoaded() || banksProvider.bankPaymentBanksLastLoadedMoreThanThreeDays()) {

            commonUIFunctions.showLoading(messageResId = R.string.loading)

            disposables.add(restService.getBankPaymentBankList()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { }
                .subscribe(
                    {
                        commonUIFunctions.dismissLoading()
                        handleGetAllBanksResponse(it)
                    },
                    {
                        commonUIFunctions.dismissLoading()
                        this.handleError(it)
                    }
                )
            )
        } else {
            var banks: List<Bank> = ArrayList()


            try {
                banks = Gson().fromJson(
                    banksProvider.getBankPaymentBanks(),
                    object : TypeToken<List<Bank>>() {}.type
                )
            } catch (e: IllegalStateException) {
                //
            }

            liveGetBankPaymentBanksResponse.value = banks
        }
    }

    private fun handleGetAllBanksResponse(bankPaymentBanksApiResponse: MonnifyApiResponse<List<Bank>>?) {

        if (bankPaymentBanksApiResponse != null && bankPaymentBanksApiResponse.isSuccessful()) {

            val banks = bankPaymentBanksApiResponse.responseBody!!

            banksProvider.saveBankPaymentBanks(banks)
            liveGetBankPaymentBanksResponse.value = banks
        }
    }

    fun setAccountNumber(value: String) {
        accountNumber = value
    }

    fun activateButton(value: Boolean) {
        liveEventActivateContinueButton.value = value
    }

    private fun verifyAccountDetails() {
        bankPaymentVerifyRequest =
            BankPaymentVerifyRequest(
                transactionReference = transactionReference,
                accountNumber = accountNumber,
                bankCode = selectedBank?.code
            )

        Logger.log(this, "verifyAccountDetails was called")

        liveEventSetContinueButtonLoading.value = true
        disposables.add(restService.bankPaymentVerifyAccountNumber(bankPaymentVerifyRequest)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {}
            .subscribe(
                {
                    liveEventSetContinueButtonLoading.value = false
                    handleVerifyAccountNumberResponse(it)
                },
                {
                    liveEventSetContinueButtonLoading.value = false
                    this.handleError(it)                }
            )
        )
    }

    private fun chargeAccountNumber(includeValidationDetails: Boolean) {
        Logger.log(this, "chargeAccountNumber was called")

        bankPaymentChargeRequest = BankPaymentChargeRequest(
            transactionReference = transactionReference,
            bankCode = selectedBank?.code,
            accountNumber = accountNumber,
            dob = if (includeValidationDetails) dateOfBirth else null,
            bvn = if (includeValidationDetails) bvn else null,
            apiKey = localMerchantKeyProvider.getApiKey(),
            collectionChannel = Constants.COLLECTION_CHANNEL
        )

        Logger.log(this, "chargeAccountNumber was called")

        liveEventSetContinueButtonLoading.value = true

        disposables.add(restService.bankPaymentChargeAccount(bankPaymentChargeRequest)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {}
            .subscribe(
                {
                    liveEventSetContinueButtonLoading.value = false
                    handleChargeAccountNumber(it)
                },
                {
                    liveEventSetContinueButtonLoading.value = false
                    this.handleError(it)
                }
            )
        )
    }

    private fun handleChargeAccountNumberError() {
        liveError.postValue(Event("An unexpected error occurred, please try again."))
    }

    private fun authorizePaymentOtp() {
        Logger.log(this, "authorizePaymentOtp was called")

        bankPaymentAuthorizeRequest = BankPaymentAuthorizeRequest(
            transactionReference = bankPaymentChargeResponse.transactionReference,
            providerReference = bankPaymentChargeResponse.providerReference,
            apiKey = localMerchantKeyProvider.getApiKey(),
            collectionChannel = Constants.COLLECTION_CHANNEL,
            token = token
        )

        Logger.log(this, "authorizePaymentOtp was called")

        commonUIFunctions.showLoading(messageResId = R.string.processing_transaction)
        disposables.add(restService.bankPaymentAuthorizePayment(bankPaymentAuthorizeRequest)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {}
            .subscribe(
                {
                    commonUIFunctions.dismissLoading()
                    handleAuthorizePaymentOtp(it)
                },
                {
                    commonUIFunctions.dismissLoading()
                    handleAuthorizePaymentOtpFailed()
                }
            )
        )
    }

    private fun handleAuthorizePaymentOtpFailed() {
        verifyTransaction(shouldComplete = true)
    }

    private fun handleAuthorizePaymentOtp(bankPaymentAuthorizeOtpApiResponse: MonnifyApiResponse<BankPaymentAuthorizeResponse>?) {
        Logger.log(this, "handleAuthorizePaymentOtp was called")

        if (bankPaymentAuthorizeOtpApiResponse != null && bankPaymentAuthorizeOtpApiResponse.isSuccessful()) {

            bankPaymentAuthorizeResponse = bankPaymentAuthorizeOtpApiResponse.responseBody!!

            verifyTransaction(shouldComplete = true)
        }
    }

    private fun handleChargeAccountNumber(bankPaymentChargeApiResponse: MonnifyApiResponse<BankPaymentChargeResponse>?) {
        Logger.log(this, "handleChargeAccountNumber was called")

        if (bankPaymentChargeApiResponse != null && bankPaymentChargeApiResponse.isSuccessful()) {

            bankPaymentChargeResponse = bankPaymentChargeApiResponse.responseBody!!

            val status: BankPaymentChargeResponse.Status =
                BankPaymentChargeResponse.Status.valueOf(
                    bankPaymentChargeResponse.status ?: BankPaymentChargeResponse.Status.FAILED.name
                )

            when (status) {
                BankPaymentChargeResponse.Status.SUCCESS -> {
                    verifyTransaction(shouldComplete = true)
                }
                BankPaymentChargeResponse.Status.PENDING -> {
                    liveBankPaymentChargeAccount.value = bankPaymentChargeResponse
                }
                BankPaymentChargeResponse.Status.FAILED -> {
                    verifyTransaction(shouldComplete = false)
                }
            }
        }
    }

    private fun handleVerifyAccountNumberResponse(bankPaymentVerifyApiResponse: MonnifyApiResponse<BankPaymentVerifyResponse>?) {
        Logger.log(this, "handleVerifyAccountNumberResponse was called")

        if (bankPaymentVerifyApiResponse != null && bankPaymentVerifyApiResponse.isSuccessful()) {

            bankPaymentVerifyResponse = bankPaymentVerifyApiResponse.responseBody!!
            liveBankPaymentVerifyAccount.value = bankPaymentVerifyResponse
        }
    }

    fun setBvn(value: String) {
        bvn = value
    }

    fun setDateOfBirth(value: String) {
        dateOfBirth = value
    }

    fun setBankPaymentState(state: BankPaymentState) {
        liveSetBankPaymentState.value = state
    }

    fun processBankPaymentState() {
        when (liveSetBankPaymentState.value) {
            BankPaymentState.VALIDATE -> {
                chargeAccountNumber(includeValidationDetails = true)
            }
            BankPaymentState.CONFIRM -> {
                chargeAccountNumber(includeValidationDetails = false)
            }
            else -> {
                verifyAccountDetails()
            }
        }
    }

    fun setOtp(value: String) {
        token = value
    }

    fun shouldAuthorizePayment(value: Boolean) {
        if (value) {
            liveEventActivateOtpAuthorizeButton.postValue(true)
        }
    }

    fun authorizePayment() {
        authorizePaymentOtp()
    }

    private fun verifyTransaction(shouldComplete: Boolean) {

        commonUIFunctions.showLoading(messageResId = R.string.verifying_transaction_status)
        verifyTransactionStatus {

            commonUIFunctions.dismissLoading()

            it?.paymentMethod = PaymentMethod.DIRECT_DEBIT.name

            handleTransactionStatusResponse(
                it, shouldForceTerminate = shouldComplete,
                shouldShowMessage = true
            )
        }
    }

}