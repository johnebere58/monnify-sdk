package com.teamapt.monnify.sdk.module.vm

import androidx.lifecycle.MutableLiveData
import android.os.CountDownTimer
import com.teamapt.monnify.sdk.Monnify
import com.teamapt.monnify.sdk.Status
import com.teamapt.monnify.sdk.data.Event
import com.teamapt.monnify.sdk.data.model.PaymentStatus
import com.teamapt.monnify.sdk.data.model.TransactionStatusDetails
import com.teamapt.monnify.sdk.rest.data.response.TransactionStatusResponse
import com.teamapt.monnify.sdk.service.StompService
import com.teamapt.monnify.sdk.util.Logger
import com.teamapt.monnify.sdk.util.default
import com.teamapt.monnify.sdk.util.isInternetConnectionError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage
import java.util.*

typealias OnTransactionStatus = (TransactionStatusResponse?) -> Unit

abstract class BaseActiveViewModel : BaseViewModel() {

    // Assumed polling duration is 30 minutes.

    private val pollingTimerDuration = 1000 * 60 * 30
    private val pollingInterval = 15 * 1000 // polling interval is 15 seconds.
    private val stompService = StompService.getInstance(Monnify.instance.getEnvironment())!!

    val activeListeningState = MutableLiveData<Boolean>().default(true)

    private var forcePollingEnabled = true

    private var countDownTimer: CountDownTimer? = null
    private var isPollingEnabled = forcePollingEnabled
    private var lastPollTime = -1L
    private var doActiveStatusRequestExists = false
    private var pollingRequestCount = 0
    protected var isTransactionComplete = false


    private fun setupWebSocketConnection() {

        Logger.log(this, "setupWebSocketConnection was called")

        if (stompService.isStompConnected)
            return

        disposables.add(stompService.connectStomp()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Logger.log(this, "Event Emitted ${it.type}}")

                when (it.type) {

                    LifecycleEvent.Type.OPENED -> {
                        activeListeningState.value = true
                        commonUIFunctions.showToastMessage("Stomp connection opened")

                        subscribeToTransaction()
                    }

                    LifecycleEvent.Type.ERROR -> {
                        commonUIFunctions.showToastMessage("Stomp connection error")
                        Logger.log(this, it.message)

                        if (!isPollingEnabled && !isTransactionComplete) {
                            startPolling()
                        }
                    }

                    LifecycleEvent.Type.CLOSED -> {
                        commonUIFunctions.showToastMessage("Stomp connection closed")

                        if (!isPollingEnabled && !isTransactionComplete) {
                            startPolling()
                        }
                    }

                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                        commonUIFunctions.showToastMessage("Stomp failed server heartbeat")
                    }

                    else -> {
                    }
                }
            },
                {
                    Logger.log(this, "Error on subscribe to lifecycle: ${it.message}")
                }
            )
        )
    }

    private fun subscribeToTransaction() {
        Logger.log(this, "subscribeToTransaction method called ${System.identityHashCode(this)}")
        stompService.subscribeOnStomp(transactionReference)
        disposables.add(stompService.subscribeOnStomp(transactionReference)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                Logger.log(this, "Stomp subscribed to $transactionReference")
                commonUIFunctions.showToastMessage("Stomp subscribed to $transactionReference")

                if (isPollingEnabled) {
                    endPolling()
                }
            }
            .subscribe(
                { topicMessage ->
                    handleStompPaymentAlert(topicMessage)
                },
                { throwable ->
                    Logger.log(
                        this,
                        "Error on subscribing to transaction: ${throwable.message}"
                    )
                    Logger.log(this, throwable.message)
                }
            )
        )
    }

    private fun pollingTimerFired() {

        val timeStampNow = Date().time

        Logger.log(this, "pollingTimerFired was fired now at $timeStampNow last was $lastPollTime")

        if (!isPollingEnabled || isTransactionComplete) {
            stopTimer()
            return
        }

        if (doActiveStatusRequestExists || lastPollTime + pollingInterval > timeStampNow) {
            return
        }

        pollingRequestCount++
        lastPollTime = timeStampNow

        verifyTransactionStatus {

            handleTransactionStatusResponse(
                it,
                shouldForceTerminate = false,
                shouldShowMessage = false
            )

            if (!isTransactionComplete && !forcePollingEnabled) {
                // Attempt to connect to socket again.
                stompService.reconnect()
            }
        }
    }

    private fun startPolling() {

        Logger.log(this, "Start Polling was called")

        isPollingEnabled = true

        countDownTimer =
            object : CountDownTimer(pollingTimerDuration.toLong(), pollingInterval.toLong()) {

                override fun onTick(millisUntilFinished: Long) {
                    pollingTimerFired()
                }

                override fun onFinish() {
                    //
                }
            }.start()

        pollingTimerFired()
    }

    private fun endPolling() {
        Logger.log(this, "End Polling was called")

        isPollingEnabled = false
        stopTimer()
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
    }

    fun startListening() {

        Logger.log(this, "startListening was called")

        if (isPollingEnabled) {
            startPolling()
        } else {
            setupWebSocketConnection()
        }
    }

    fun stopListening() {

        isTransactionComplete = true

        if (isPollingEnabled) {
            stopTimer()
        } else {
            if (stompService.isStompConnected) {
                stompService.disconnect()
            }
        }
    }

    protected fun verifyTransactionStatus(completion: OnTransactionStatus) {

        if (doActiveStatusRequestExists) return

        doActiveStatusRequestExists = true

        disposables.add(restService.checkTransactionStatus(
            localMerchantKeyProvider.getApiKey(),
            transactionReference
        )
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {}
            .subscribe(
                {
                    doActiveStatusRequestExists = false

                    if (it == null || !it.isSuccessful() || it.responseBody == null) {
                        return@subscribe
                    }

                    activeListeningState.value = true

                    completion(it.responseBody!!)
                },
                {
                    // Silent Failure as this status check is for polling.
                    doActiveStatusRequestExists = false

                    if (it.isInternetConnectionError()) {
                        activeListeningState.value = false
                        completion(null)
                    }
                })
        )
    }

    private fun handleStompPaymentAlert(topicMessage: StompMessage) {
        Logger.log(this, "Stomp alert received $topicMessage ${topicMessage.payload}")

        verifyTransactionStatus {

            handleTransactionStatusResponse(
                it,
                shouldForceTerminate = true,
                shouldShowMessage = false
            )
        }
    }

    protected fun handleTransactionStatusResponse(
        transaction: TransactionStatusResponse?,
        shouldForceTerminate: Boolean,
        shouldShowMessage: Boolean,
        message: String? = null
    ) {

        if (transaction == null) {
            commonUIFunctions.dismissLoading()
            if (shouldShowMessage) {
                commonUIFunctions.showToastMessage(message = "Please check your internet connection")
            }
            return
        }

        val paymentStatus = if (transaction.paymentStatus != null)
            PaymentStatus.valueOf(transaction.paymentStatus!!) else PaymentStatus.PENDING

        updateTransactionResult(transaction)

        if (shouldForceTerminate || paymentStatus != PaymentStatus.PENDING) {

            commonUIFunctions.dismissLoading()

            stopListening()

            showTransactionStatus(transaction)
        } else {
            if (shouldShowMessage) {
                commonUIFunctions.showToastMessage(message = "Payment not yet received.")

                /**  Called liveError to displace error on the fragment  */
                liveError.postValue(Event(message ?: "Payment was not found"))
            }
        }
    }

    private fun showTransactionStatus(transaction: TransactionStatusResponse) {
        Logger.log(this, "showTransactionStatus $transaction was called")

        val paymentStatus = if (transaction.paymentStatus != null)
            PaymentStatus.valueOf(transaction.paymentStatus!!)
        else PaymentStatus.PENDING

        val transactionStatusDetails =
            TransactionStatusDetails.Builder().isCompleted(
                paymentStatus == PaymentStatus.PAID
                        || paymentStatus == PaymentStatus.OVERPAID
                        || paymentStatus == PaymentStatus.PARTIALLY_PAID
            )
                .status(Status.get(paymentStatus))
                .amountPaid(transaction.amount)
                .amountPayable(transaction.payableAmount)
                .transactionReference(transactionReference)
                .currencyCode(transaction.currencyCode)
                .paymentMethod(transaction.paymentMethod)
                .build()

        Logger.log(this, "openTransactionStatusFragment was called")

        navigator.openTransactionStatusFragment(transactionStatusDetails)
    }

    override fun onCleared() {
        super.onCleared()
        stompService.disconnect()
    }
}

