package com.teamapt.monnify.monnify_payment_sdk.module.vm

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.monnify.monnify_payment_sdk.R
import com.teamapt.monnify.monnify_payment_sdk.Status
import com.teamapt.monnify.monnify_payment_sdk.data.Event
import com.teamapt.monnify.monnify_payment_sdk.data.SingleLiveEvent
import com.teamapt.monnify.monnify_payment_sdk.data.model.PaymentMethod
import com.teamapt.monnify.monnify_payment_sdk.data.model.TransactionStatusDetails
import com.teamapt.monnify.monnify_payment_sdk.rest.data.request.Card
import com.teamapt.monnify.monnify_payment_sdk.rest.data.request.InitializeTransactionRequest
import com.teamapt.monnify.monnify_payment_sdk.rest.data.response.InitializeTransactionResponse
import com.teamapt.monnify.monnify_payment_sdk.rest.data.response.MonnifyApiResponse
import com.teamapt.monnify.monnify_payment_sdk.util.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal

class PaymentMethodsViewModel : BaseViewModel() {

    lateinit var initializeTransactionRequest: InitializeTransactionRequest
    lateinit var initializeTransactionResponse: InitializeTransactionResponse
    val liveEventInitializeTransaction = SingleLiveEvent<Any>()

    val liveSkipToPaymentMethod = SingleLiveEvent<PaymentMethod>()

    val paymentMethodList = arrayListOf<PaymentMethod>()

    val liveInitializeTransactionResponse = MutableLiveData<InitializeTransactionResponse>()

    val liveAmountBeingPaid = MutableLiveData<BigDecimal>()

    private var selectedPaymentMethod: PaymentMethod? = null

    var savedCardForPayWithSameCard: Card? = null

    override fun init(bundle: Bundle) {
        initializeTransactionRequest = InitializeTransactionRequest(
            bundle.getParcelable(Constants.ARG_TRANSACTION_DETAILS)!!,
            localMerchantKeyProvider.getApiKey(),
            localMerchantKeyProvider.getContractCode()
        )

        liveAmountBeingPaid.value = initializeTransactionRequest.amount
        initializeTransaction()
    }

    private fun initializeTransaction() {

        commonUIFunctions.showLoading(R.string.please_wait)
        disposables.add(restService.initializeTransaction(initializeTransactionRequest)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .subscribe(
                {
                    commonUIFunctions.dismissLoading()
                    handleInitializeTransactionResponse(it)
                },
                {
                    commonUIFunctions.dismissLoading()
                    this.handleError(it)
                }
            )
        )
    }

    private fun handleInitializeTransactionResponse(
        initializeTransactionApiResponse: MonnifyApiResponse<InitializeTransactionResponse>?
    ) {
        if (initializeTransactionApiResponse != null && initializeTransactionApiResponse.isSuccessful()) {
            initializeTransactionResponse = initializeTransactionApiResponse.responseBody!!
            transactionReference = initializeTransactionResponse.transactionReference

            liveInitializeTransactionResponse.value = initializeTransactionResponse

            addPaymentMethodsToList()
            checkPaymentMethods()
        }
    }

    private fun checkPaymentMethods() {
        when {
            paymentMethodList.isEmpty() -> {
                val transactionStatusDetails =
                    TransactionStatusDetails.Builder().status(Status.CANCELLED)
                        .message("No payment method currently enabled.").build()

                navigator.openTransactionStatusFragment(transactionStatusDetails)
            }

            paymentMethodList.size == 1 -> {
                setCurrentPaymentMethod(paymentMethodList.first())
                liveSkipToPaymentMethod.postValue(paymentMethodList.first())
            }
            paymentMethodList.size > 1 -> {
                liveEventInitializeTransaction.call()
            }
        }
    }

    private fun addPaymentMethodsToList() {
        paymentMethodList.clear()
        for (method in initializeTransactionResponse.enabledPaymentMethod ?: return) {
            if (PaymentMethod.contains(method)) {
                paymentMethodList.add(PaymentMethod.valueOf(method))
            }
        }
    }

    fun updatePaymentMethodsList(value: PaymentMethod?): ArrayList<PaymentMethod> {
        val list = arrayListOf<PaymentMethod>()

        for (method in paymentMethodList) {
            if (method == value)
                continue
            list.add(method)
        }

        return list
    }

    fun setCurrentPaymentMethod(value: PaymentMethod) {
        selectedPaymentMethod = value
    }

    fun getCurrentPaymentMethod(): PaymentMethod? {
        return selectedPaymentMethod
    }

    fun saveCard(card: Card?) {
        savedCardForPayWithSameCard = card
    }
}