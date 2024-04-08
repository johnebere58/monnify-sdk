package com.monnify.monnify_payment_sdk.module.vm

import android.os.Bundle
import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.monnify.monnify_payment_sdk.R
import com.monnify.monnify_payment_sdk.data.SingleLiveEvent
import com.monnify.monnify_payment_sdk.data.model.PaymentMethod
import com.monnify.monnify_payment_sdk.rest.data.request.InitializeTransferPaymentRequest
import com.monnify.monnify_payment_sdk.rest.data.response.Bank
import com.monnify.monnify_payment_sdk.rest.data.response.InitializeTransferPaymentResponse
import com.monnify.monnify_payment_sdk.rest.data.response.MonnifyApiResponse
import com.monnify.monnify_payment_sdk.util.BanksProvider
import com.monnify.monnify_payment_sdk.util.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TransferPaymentViewModel(private val banksProvider: BanksProvider) : BaseActiveViewModel() {

    private lateinit var initializeTransferPaymentRequest: InitializeTransferPaymentRequest
    lateinit var initializeTransferPaymentResponse: InitializeTransferPaymentResponse

    val liveBankTransferResponse = SingleLiveEvent<InitializeTransferPaymentResponse?>()
    val liveGetAllBanksResponse = MutableLiveData<List<Bank>>()
    val liveAccountDurationOver = SingleLiveEvent<Boolean>()

    val liveVerifyingTransaction = SingleLiveEvent<Boolean>()

    override fun init(bundle: Bundle) {
        Logger.log(this, "init was called in ${this.javaClass.simpleName}")

        initializeBankTransfer()
    }

    private fun initializeBankTransfer() {
        initializeTransferPaymentRequest = InitializeTransferPaymentRequest(
            transactionReference = transactionReference,
            apiKey = localMerchantKeyProvider.getApiKey()
        )

        Logger.log(this, "initializeBankTransfer was called")

        commonUIFunctions.showLoading(R.string.initializing_transaction)
        disposables.add(restService.initializeTransferPayment(initializeTransferPaymentRequest)
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
                    this.handleError(it)
                }
            )
        )
    }

    private fun startOneMinuteCountdown() {
        object : CountDownTimer(60000, 60000) {
            override fun onTick(millisUntilFinished: Long) {
                Logger.log(this, "millisUntilFinished $millisUntilFinished")
            }

            override fun onFinish() {
                liveAccountDurationOver.value = true //postValue(true)
            }
        }.start()
    }

    private fun loadAllBanksMakeApiCallIfRequired() {
        if (banksProvider.banksNotLoaded() || banksProvider.banksLastLoadedMoreThanThirtyDays()) {

            disposables.add(restService.getAllBanks()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { }
                .subscribe(
                    {
                        handleGetAllBanksResponse(it)
                    },
                    {
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
                //
            }

            liveGetAllBanksResponse.value = banks
        }
    }

    private fun handleInitializeBankTransferResponse(
        initializeTransferApiPaymentResponse: MonnifyApiResponse<InitializeTransferPaymentResponse>?
    ) {

        Logger.log(this, "handleInitializeBankTransferResponse was called")

        if (initializeTransferApiPaymentResponse != null && initializeTransferApiPaymentResponse.isSuccessful()) {

            initializeTransferPaymentResponse = initializeTransferApiPaymentResponse.responseBody!!
            liveBankTransferResponse.value = initializeTransferPaymentResponse

            isTransactionComplete = false
            loadAllBanksMakeApiCallIfRequired()

            startListening()
        }
    }

    private fun handleGetAllBanksResponse(getAllBanksResponse: MonnifyApiResponse<List<Bank>>?) {

        if (getAllBanksResponse != null && getAllBanksResponse.isSuccessful()) {

            val banks = getAllBanksResponse.responseBody!!

            banksProvider.saveBanks(banks)
            liveGetAllBanksResponse.value = banks
        }
    }

    val countDownCompleteLambda: () -> Unit = {
        Logger.log(this, "countDownCompleteLambda was called")
        commonUIFunctions.showToastMessage("Time up!")
        liveAccountDurationOver.value = true
        verifyTransaction(shouldComplete = true)
    }

    fun verifyTransaction(shouldComplete: Boolean) {

        liveVerifyingTransaction.value = true

        verifyTransactionStatus {
            liveVerifyingTransaction.value = false

            it?.paymentMethod = PaymentMethod.ACCOUNT_TRANSFER.name

            handleTransactionStatusResponse(
                it, shouldForceTerminate = shouldComplete,
                shouldShowMessage = true,
                message = "Payment was not found, please transfer to the account number below"
            )
        }
    }
}