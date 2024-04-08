package com.monnify.monnify_payment_sdk.module.vm

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.monnify.monnify_payment_sdk.Monnify
import com.monnify.monnify_payment_sdk.R
import com.monnify.monnify_payment_sdk.Status
import com.monnify.monnify_payment_sdk.data.Event
import com.monnify.monnify_payment_sdk.data.model.*
import com.monnify.monnify_payment_sdk.rest.data.request.*
import com.monnify.monnify_payment_sdk.rest.data.response.CardPaymentResponse
import com.monnify.monnify_payment_sdk.rest.data.response.CardRequirementsResponse
import com.monnify.monnify_payment_sdk.rest.data.response.InitializeCardPaymentResponse
import com.monnify.monnify_payment_sdk.rest.data.response.MonnifyApiResponse
import com.monnify.monnify_payment_sdk.util.CardValidator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal

class CardPaymentViewModel : BaseActiveViewModel() {

    val liveCardValidation = MutableLiveData<Boolean>()
    val liveCardNumberValidation = MutableLiveData<Boolean>()
    val liveCardType = MutableLiveData<CardType>()
    val liveCardExpiryDateValidation = MutableLiveData<Boolean>()
    val liveCardCvvValidation = MutableLiveData<Boolean>()
    val liveCardPinValidation = MutableLiveData<Boolean>()
    val liveOtpValidation = MutableLiveData<Boolean>()
    val liveCardOtpAuthorizationError = MutableLiveData<String>()
    val liveAmountPayable = MutableLiveData<BigDecimal>()
    val liveAmount = MutableLiveData<BigDecimal>()
    val livePaymentFee = MutableLiveData<BigDecimal>()
    val liveCardPinRequired = MutableLiveData<Boolean>()

    val liveOpenOtpFragment = MutableLiveData<Boolean>()
    val liveOpenSecure3DAuthFragment = MutableLiveData<Boolean>()

    lateinit var cardPaymentResponse: CardPaymentResponse

    lateinit var currentCard: Card

    private var pinIsRequired = false

    private var otp: String = ""

    override fun init(bundle: Bundle) {
        initializeCardPayment()
        currentCard = Card()
    }

    fun setCardNumberAndVerify(number: String?) {
        currentCard.number = number
        verifyCardNumber()
    }

    fun setCardCvvAndVerify(cvv: String?) {
        currentCard.cvv = cvv
        verifyCardCvv()
    }

    fun setCardPinAndVerify(pin: String?) {
        currentCard.pin = pin
        verifyCardPin()
    }

    fun setCardExpiryAndVerify(cardExpiry: String?) {

        try {
            val cardExpiryStringSplit = cardExpiry?.split("/")
            if (cardExpiryStringSplit?.size != 2) {
                currentCard.expiryMonth = 0
                currentCard.expiryYear = 0
                verifyCardExpiry()
                return
            }
            currentCard.expiryMonth = cardExpiryStringSplit[0].toInt()
            currentCard.expiryYear = ("20" + cardExpiryStringSplit[1]).toInt()
            verifyCardExpiry()
        } catch (e: Exception) {
            currentCard.expiryMonth = 0
            currentCard.expiryYear = 0
            verifyCardExpiry()
            return
        }
    }

    fun setOtpAndVerify(otp: String) {
        verifyOtp(otp)
    }

    fun setOtp(value: String) {
        otp = value
    }

    fun shouldAllowOtpValidation(value: Boolean) {
        liveOtpValidation.value = value
    }

    private fun verifyCardNumber(verifyCard: Boolean = true): Boolean {
        val result = CardValidator.verifyCardNumber(currentCard.number)
        val cardType = CardType.detect(currentCard.number)
        if (liveCardType.value != cardType) {
            liveCardType.value = cardType
        }
        liveCardNumberValidation.value = result
        if (verifyCard)
            verifyCard()
        return result
    }

    private fun verifyCardExpiry(verifyCard: Boolean = true): Boolean {
        val result =
            CardValidator.verifyCardExpiryDate(currentCard.expiryMonth, currentCard.expiryYear)
        liveCardExpiryDateValidation.value = result
        if (verifyCard)
            verifyCard()
        return result
    }

    private fun verifyCardCvv(verifyCard: Boolean = true): Boolean {
        val result = CardValidator.verifyCardCvv(currentCard.cvv)
        liveCardCvvValidation.value = result
        if (verifyCard)
            verifyCard()
        return result
    }

    private fun verifyCardPin(verifyCard: Boolean = true): Boolean {
        val result = CardValidator.verifyCardPin(currentCard.pin)
        liveCardPinValidation.value = result
        if (verifyCard)
            verifyCard()
        return result
    }

    private fun verifyCard() {
        if (CardValidator.verifyCardNumber(currentCard.number) &&
            CardValidator.verifyCardExpiryDate(currentCard.expiryMonth, currentCard.expiryYear) &&
            CardValidator.verifyCardCvv(currentCard.cvv) &&
            (if (pinIsRequired)
                CardValidator.verifyCardPin(currentCard.pin)
            else
                true)
        ) {
            liveCardValidation.postValue(true)
        } else {
            liveCardValidation.postValue(false)
        }
    }

    fun verifyCardAndInitializePayOnSuccess() {
        if (verifyCardNumber(false) &&
            verifyCardExpiry(false) &&
            verifyCardCvv(false) &&
            (if (pinIsRequired)
                verifyCardPin(false)
            else
                true)
        ) {
            liveCardValidation.postValue(true)
            payWithCard()
        } else {
            liveCardValidation.postValue(false)
        }
    }

    private fun verifyOtp(otp: String) {
        val result = (otp.length >= 6)
        liveOtpValidation.value = result
    }

    private fun initializeCardPayment() {
        val initializeCardPaymentRequest = InitializeCardPaymentRequest(
            transactionReference = transactionReference,
            apiKey = localMerchantKeyProvider.getApiKey()
        )

        commonUIFunctions.showLoading(R.string.initializing_transaction)
        disposables.add(restService.initializeCardPayment(initializeCardPaymentRequest)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .subscribe(
                {
                    commonUIFunctions.dismissLoading()
                    handleInitializeCardPaymentResponse(it)
                },
                {
                    commonUIFunctions.dismissLoading()
                    this.handleError(it)
                }
            )
        )
    }

    fun checkCardRequirements() {
        val pan = currentCard.number
        if (pan == null || pan == "") return

        val cardRequirementRequest = CardRequirementRequest(pan = pan)

        disposables.add(
            restService.getCardRequirements(cardRequirementRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        handleCardRequirementsResponse(it)
                    },
                    {
                        this.handleError(it)
                    }
                )
        )
    }

    private fun payWithCard() {
        val cardPayRequest = CardPaymentRequest(
            transactionReference,
            localMerchantKeyProvider.getApiKey(),
            currentCard
        )
        commonUIFunctions.showLoading()
        disposables.add(
            restService.payWithCard(cardPayRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        handlePayWithCardResponse(it)
                    },
                    {
                        this.handleError(it)
                    }
                )
        )
    }

    fun authorizeCardOtp() {
        val authorizeCardOtpRequest = AuthorizeCardOtpRequest(
            transactionReference = transactionReference,
            apiKey = localMerchantKeyProvider.getApiKey(),
            tokenId = cardPaymentResponse.tokenData?.id ?: "",
            token = otp
        )

        commonUIFunctions.showLoading(R.string.waiting_for_otp_auth)
        disposables.add(restService.authorizeCardOtp(authorizeCardOtpRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    handlePayWithCardResponse(it)
                },
                {
                    this.handleError(it)
                }
            ))
    }

    fun authorizeCardSecure3D() {
        val authorizeCardSecure3DRequest = AuthorizeCardSecure3DRequest(
            transactionReference = transactionReference,
            apiKey = localMerchantKeyProvider.getApiKey()
        )

        commonUIFunctions.showLoading(R.string.waiting_for_secure_3d_auth)
        disposables.add(restService.authorizeCardSecure3D(authorizeCardSecure3DRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    // Do nothing. The payment notification is handled when a stomp notification is received
                },
                {
                    this.handleError(it)
                }
            ))
    }

    private fun handleInitializeCardPaymentResponse(
        initializeCardPaymentApiResponse: MonnifyApiResponse<InitializeCardPaymentResponse>?
    ) {

        if (initializeCardPaymentApiResponse != null && initializeCardPaymentApiResponse.isSuccessful()) {

            val totalPayable = initializeCardPaymentApiResponse.responseBody?.totalAmountPayable
            val amount = initializeCardPaymentApiResponse.responseBody?.amount
            val paymentFee = initializeCardPaymentApiResponse.responseBody?.paymentFee
            liveAmountPayable.value = totalPayable ?: liveAmountPayable.value
            liveAmount.value = amount ?: liveAmountPayable.value
            livePaymentFee.value = paymentFee ?: BigDecimal.ZERO
        }
    }

    private fun handleCardRequirementsResponse(cardRequirementsResponse: MonnifyApiResponse<CardRequirementsResponse>) {

        if (cardRequirementsResponse.isSuccessful()) {
            pinIsRequired = cardRequirementsResponse.responseBody?.requirePin ?: pinIsRequired
            liveCardPinRequired.value = pinIsRequired
        }
    }

    private fun handlePayWithCardResponse(payWithCardApiPaymentResponse: MonnifyApiResponse<CardPaymentResponse>) {
        commonUIFunctions.dismissLoading()

        if (payWithCardApiPaymentResponse.isSuccessful()) {

            val status = if (payWithCardApiPaymentResponse.responseBody?.status != null)
                CardChargeStatus.valueOf(payWithCardApiPaymentResponse.responseBody!!.status!!)
            else CardChargeStatus.PENDING

            if (status == CardChargeStatus.AUTHENTICATION_FAILED) {
                liveCardOtpAuthorizationError.postValue("Could to validate OTP. Please try again.")
                return
            }

            cardPaymentResponse = payWithCardApiPaymentResponse.responseBody!!

            when (status) {

                CardChargeStatus.SUCCESS -> {
                    cardPaymentResponse = payWithCardApiPaymentResponse.responseBody!!
                    val transactionStatusDetails = TransactionStatusDetails.Builder()
                        .isCompleted(true)
                        .status(Status.PAID)
                        .amountPaid(cardPaymentResponse.authorizedAmount)
                        .amountPayable(cardPaymentResponse.authorizedAmount)
                        .transactionReference(transactionReference)
                        .paymentMethod(PaymentMethod.CARD.name)
                        .build()

                    updateTransactionResult(cardPaymentResponse)

                    navigator.openTransactionStatusFragment(transactionStatusDetails)
                }

                CardChargeStatus.OTP_AUTHORIZATION_REQUIRED -> {
                    liveOpenOtpFragment.value = true
                }
                CardChargeStatus.MPGS_3DS_AUTHORIZATION_REQUIRED,
                CardChargeStatus.BANK_AUTHORIZATION_REQUIRED -> {
                    cardPaymentResponse = payWithCardApiPaymentResponse.responseBody!!
                    liveOpenSecure3DAuthFragment.value = true
                }

                CardChargeStatus.PENDING -> {
                    val transactionStatusDetails = TransactionStatusDetails.Builder()
                        .isCompleted(false)
                        .status(Status.PENDING)
                        .amountPayable(cardPaymentResponse.authorizedAmount)
                        .transactionReference(transactionReference)
                        .paymentMethod(PaymentMethod.CARD.toString())
                        .build()

                    updateTransactionResult(cardPaymentResponse)

                    navigator.openTransactionStatusFragment(transactionStatusDetails)
                }

                CardChargeStatus.AUTHENTICATION_FAILED, CardChargeStatus.FAILED -> {

                    val transactionStatusDetails = TransactionStatusDetails.Builder()
                        .isCompleted(false)
                        .status(Status.FAILED)
                        .message(cardPaymentResponse.message)
                        .amountPayable(cardPaymentResponse.authorizedAmount)
                        .transactionReference(transactionReference)
                        .paymentMethod(PaymentMethod.CARD.toString())
                        .build()

                    updateTransactionResult(cardPaymentResponse)

                    navigator.openTransactionStatusFragment(transactionStatusDetails)
                }

                CardChargeStatus.PIN_REQUIRED -> {
                    liveError.postValue(Event("Pin required"))
                    liveCardPinRequired.value = true
                }

            }
        } else {
            liveError.postValue(Event("Request not successful. Please try again."))
        }
    }

    fun getCardPaymentProvider(): CardChargeProvider =

        when {
            cardPaymentResponse.secure3dData?.termUrl?.contains("interswitchng.com") == true -> CardChargeProvider.IPG
            cardPaymentResponse.secure3dData?.redirectUrl?.contains("payu.co.za") == true -> CardChargeProvider.PAYU
            else -> CardChargeProvider.UNKNOWN
        }

    private fun updateTransactionResult(cardPaymentResponse: CardPaymentResponse) {

        val monnify = Monnify.instance

        monnify.paymentStatus.transactionReference = cardPaymentResponse.transactionReference ?: ""
        monnify.paymentStatus.paymentReference = cardPaymentResponse.paymentReference ?: ""

        monnify.paymentStatus.status = Status.get(
            cardChargeStatus = CardChargeStatus.valueOf(
                cardPaymentResponse.status ?: CardChargeStatus.PENDING.name
            )
        )

        monnify.paymentStatus.paymentMethod = TransactionType.CARD
        monnify.paymentStatus.amountPaid = cardPaymentResponse.authorizedAmount ?: BigDecimal.ZERO
        monnify.paymentStatus.amountPayable =
            cardPaymentResponse.authorizedAmount ?: BigDecimal.ZERO
        monnify.paymentStatus.message = cardPaymentResponse.message
    }
}
