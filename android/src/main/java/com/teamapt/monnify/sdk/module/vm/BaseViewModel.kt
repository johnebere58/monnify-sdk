package com.teamapt.monnify.sdk.module.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.os.Bundle
import com.teamapt.monnify.sdk.Monnify
import com.teamapt.monnify.sdk.MonnifyTransactionResponse
import com.teamapt.monnify.sdk.data.Event
import com.teamapt.monnify.sdk.module.CommonUIFunctions
import com.teamapt.monnify.sdk.module.IAppNavigator
import com.teamapt.monnify.sdk.module.LocalMerchantKeyProvider
import com.teamapt.monnify.sdk.rest.data.response.TransactionStatusResponse
import com.teamapt.monnify.sdk.service.RestService
import com.teamapt.monnify.sdk.util.ErrorUtils
import com.teamapt.monnify.sdk.util.Logger
import com.teamapt.monnify.sdk.util.readableErrorMessage
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel : ViewModel() {

    var transactionReference: String = ""
    protected val restService = RestService.getInstance(Monnify.instance.getEnvironment())!!
    protected val errorUtils = ErrorUtils()

    protected lateinit var navigator: IAppNavigator
    protected lateinit var commonUIFunctions: CommonUIFunctions
    protected lateinit var localMerchantKeyProvider: LocalMerchantKeyProvider

    val liveError = MutableLiveData<Event<String>>()

    protected val disposables = CompositeDisposable()

    fun init(
        bundle: Bundle,
        localMerchantKeyProvider: LocalMerchantKeyProvider,
        navigator: IAppNavigator,
        commonUIFunctions: CommonUIFunctions
    ) {
        this.localMerchantKeyProvider = localMerchantKeyProvider
        this.navigator = navigator
        this.commonUIFunctions = commonUIFunctions

        init(bundle)
    }

    protected open fun handleError(throwable: Throwable) {
        commonUIFunctions.dismissLoading()
        liveError.postValue(Event(throwable.readableErrorMessage()))
        Logger.log(this, throwable.message)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    abstract fun init(bundle: Bundle)

    fun updateTransactionResult(transaction: TransactionStatusResponse) {
        val monnify = Monnify.instance
        monnify.paymentStatus = MonnifyTransactionResponse.fromTransactionResponse(monnify.paymentStatus, transaction)
    }

}