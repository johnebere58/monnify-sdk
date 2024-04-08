package com.teamapt.monnify.monnify_payment_sdk.module.vm

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.teamapt.monnify.monnify_payment_sdk.data.SingleLiveEvent
import com.teamapt.monnify.monnify_payment_sdk.data.model.PaymentMethod
import com.teamapt.monnify.monnify_payment_sdk.rest.data.request.InitializePhonePaymentRequest
import com.teamapt.monnify.monnify_payment_sdk.rest.data.response.InitializePhonePaymentResponse
import com.teamapt.monnify.monnify_payment_sdk.rest.data.response.MonnifyApiResponse
import com.teamapt.monnify.monnify_payment_sdk.util.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PhonePaymentViewModel : BaseActiveViewModel() {

    private lateinit var initializePhonePaymentRequest: InitializePhonePaymentRequest
    private lateinit var initializePhonePaymentResponse: InitializePhonePaymentResponse

    val liveActivateContinueButton = SingleLiveEvent<Boolean>()
    val liveEventHasInitializePayment = SingleLiveEvent<Boolean>()
    val liveVerifyingTransaction = MutableLiveData<Boolean>()

    private var phoneNumber: String = ""

    override fun init(bundle: Bundle) {
        liveEventHasInitializePayment.value = true
    }

    fun setPhoneNumber(value: String) {
        phoneNumber = value
    }

    fun activateContinueButton(validator: Boolean) {
        liveActivateContinueButton.value = validator
    }

    fun initializePhoneNumberPayment() {
        Logger.log(this, "initializePhoneNumberPayment was called")

        initializePhonePaymentRequest = InitializePhonePaymentRequest(
            transactionReference = transactionReference,
            phoneNumber = phoneNumber
        )

        disposables.add(restService.initializePhonePayment(initializePhonePaymentRequest)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {}
            .subscribe(
                {
                    handleInitializePhonePaymentResponse(it)
                },
                {
                    handleInitializePhonePaymentError(it)
                })
        )
    }

    private fun handleInitializePhonePaymentError(it: Throwable?) {
    }

    private fun handleInitializePhonePaymentResponse(initializePhonePaymentApiResponse: MonnifyApiResponse<InitializePhonePaymentResponse>?) {

        Logger.log(this, "handleInitializePhonePaymentResponse was called")

        if (initializePhonePaymentApiResponse != null && initializePhonePaymentApiResponse.isSuccessful()) {

            initializePhonePaymentResponse = initializePhonePaymentApiResponse.responseBody!!

            verifyTransaction(shouldComplete = true)
        }
    }

    fun verifyTransaction(shouldComplete: Boolean) {

        liveVerifyingTransaction.value = true
        verifyTransactionStatus {

            liveVerifyingTransaction.value = false

            it?.paymentMethod = PaymentMethod.PHONE_NUMBER.name

            handleTransactionStatusResponse(
                it,
                shouldForceTerminate = shouldComplete,
                shouldShowMessage = true
            )
        }
    }
}