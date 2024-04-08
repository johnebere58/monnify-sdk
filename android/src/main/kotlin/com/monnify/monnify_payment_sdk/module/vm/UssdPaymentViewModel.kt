package com.monnify.monnify_payment_sdk.module.vm

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.monnify.monnify_payment_sdk.R
import com.monnify.monnify_payment_sdk.data.Event
import com.monnify.monnify_payment_sdk.data.SingleLiveEvent
import com.monnify.monnify_payment_sdk.data.model.PaymentMethod
import com.monnify.monnify_payment_sdk.rest.data.request.InitializeUssdPaymentRequest
import com.monnify.monnify_payment_sdk.rest.data.response.Bank
import com.monnify.monnify_payment_sdk.rest.data.response.InitializeUssdPaymentResponse
import com.monnify.monnify_payment_sdk.rest.data.response.MonnifyApiResponse
import com.monnify.monnify_payment_sdk.util.BanksProvider
import com.monnify.monnify_payment_sdk.util.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UssdPaymentViewModel(private val banksProvider: BanksProvider) : BaseActiveViewModel() {

    private lateinit var initializeUssdPaymentRequest: InitializeUssdPaymentRequest
    private lateinit var initializeUssdPaymentResponse: InitializeUssdPaymentResponse

    val liveGetSelectedBank = MutableLiveData<Event<Bank>>()
    val liveGetAllBanksResponse = MutableLiveData<List<Bank>>()
    val liveUssdPaymentResponse = MutableLiveData<InitializeUssdPaymentResponse>()
    val liveVerifyingTransaction = SingleLiveEvent<Boolean>()


    var selectedBank: Bank? = null


    override fun init(bundle: Bundle) {
        Logger.log(this, "init was called in ${this.javaClass.simpleName}")

        loadAllBanks()
    }

    fun loadAllBanks() {
        loadAllBanksMakeApiCallIfRequired()
    }

    private fun loadAllBanksMakeApiCallIfRequired() {
        if (banksProvider.banksNotLoaded() || banksProvider.banksLastLoadedMoreThanThirtyDays()) {

            commonUIFunctions.showLoading(R.string.initializing_transaction)
            disposables.add(restService.getAllBanks()
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
                    banksProvider.getAllBanks(),
                    object : TypeToken<List<Bank>>() {}.type
                )
            } catch (e: IllegalStateException) {

            }

            liveGetAllBanksResponse.value = banks
        }
    }

    private fun handleGetAllBanksResponse(getAllBanksResponse: MonnifyApiResponse<List<Bank>>?) {
        if (getAllBanksResponse != null && getAllBanksResponse.isSuccessful()) {

            val banks: List<Bank> = getAllBanksResponse.responseBody!!

            banksProvider.saveBanks(banks)
            liveGetAllBanksResponse.value = banks
        }

    }

    fun initializeUssdPayment(bankUssdCode: String) {

        Logger.log(this, "initializeUssdPayment was called")

        initializeUssdPaymentRequest = InitializeUssdPaymentRequest(
            transactionReference = transactionReference,
            bankUssdCode = bankUssdCode
        )
        commonUIFunctions.showLoading(R.string.processing_transaction)
        disposables.add(restService.initializeUssdPayment(initializeUssdPaymentRequest)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {}
            .subscribe(
                {
                    commonUIFunctions.dismissLoading()
                    handleInitializeUssdPaymentResponse(it)
                },
                {
                    commonUIFunctions.dismissLoading()
                    this.handleError(it)
                })
        )
    }

    private fun handleInitializeUssdPaymentResponse(initializeUssdPaymentApiResponse: MonnifyApiResponse<InitializeUssdPaymentResponse>?) {

        Logger.log(this, "handleInitializeUssdPaymentResponse was called")

        if (initializeUssdPaymentApiResponse != null && initializeUssdPaymentApiResponse.isSuccessful()) {

            initializeUssdPaymentResponse = initializeUssdPaymentApiResponse.responseBody!!
            liveUssdPaymentResponse.value = initializeUssdPaymentResponse

            isTransactionComplete = false

            startListening()
        }
    }

    fun verifyTransaction(shouldComplete: Boolean) {


        liveVerifyingTransaction.value = true
        verifyTransactionStatus {

            liveVerifyingTransaction.value = false

            it?.paymentMethod = PaymentMethod.USSD.name

            handleTransactionStatusResponse(
                it, shouldForceTerminate = shouldComplete,
                shouldShowMessage = true
            )
        }
    }

    fun onBankSelected(bank: Bank) {
        selectedBank = bank
        liveGetSelectedBank.value = Event(bank)
    }

}