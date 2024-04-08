package com.teamapt.monnify.sdk.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import com.monnify.monnify_payment_sdk.R
import com.teamapt.monnify.sdk.data.model.PaymentMethod
import com.teamapt.monnify.sdk.util.Logger

class MonnifyTransactionStatusButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayoutCompat(context, attrs) {

    private var cardLayout: LinearLayoutCompat
    private var transferLayout: LinearLayoutCompat
    private var ussdLayout: LinearLayoutCompat
    private var bankLayout: LinearLayoutCompat
    private var phoneNumberLayout: LinearLayoutCompat

    private var payWithSameCardButton: AppCompatButton
    private var payWithAnotherCardButton: AppCompatButton
    private var payWithTransferButton: AppCompatButton
    private var payWithUssdButton: AppCompatButton
    private var payWithBankButton: AppCompatButton
    private var payWithPhoneNumberButton: AppCompatButton
    private var payWithOtherMethodsButton: AppCompatButton

    var eventListener: MonnifyTransactionStatusButtonClickListener? = null

    interface MonnifyTransactionStatusButtonClickListener {
        fun onPayWithSameCard()
        fun onPayWithAnotherCard()
        fun onPayWithTransfer()
        fun onPayWithUssd()
        fun onPayWithBank()
        fun onPayWithPhoneNumber()
        fun onPayWithOtherMethods()
    }

    init {
        inflate(context, R.layout.plugin_monnify_transaction_status_button, this)

        cardLayout = findViewById(R.id.payWithCardLayout)
        transferLayout = findViewById(R.id.payWithTransferLayout)
        ussdLayout = findViewById(R.id.payWithUssdLayout)
        bankLayout = findViewById(R.id.payWithBankLayout)
        phoneNumberLayout = findViewById(R.id.payWithPhoneNumberLayout)

        payWithSameCardButton = findViewById(R.id.payWithSameCardButton)
        payWithAnotherCardButton = findViewById(R.id.payWithAnotherCardButton)
        payWithTransferButton = findViewById(R.id.payWithTransferButton)
        payWithUssdButton = findViewById(R.id.payWithUssdButton)
        payWithBankButton = findViewById(R.id.payWithBankButton)
        payWithPhoneNumberButton = findViewById(R.id.payWithPhoneNumberButton)
        payWithOtherMethodsButton = findViewById(R.id.payWithOtherMethodsButton)

        setupEventListener()
    }

    fun setupView(method: PaymentMethod? = null) {
        when (method) {
            PaymentMethod.CARD -> {
                cardLayout.visibility = VISIBLE
            }
            PaymentMethod.ACCOUNT_TRANSFER -> {
                transferLayout.visibility = VISIBLE
            }
            PaymentMethod.DIRECT_DEBIT -> {
                bankLayout.visibility = VISIBLE
            }
            PaymentMethod.USSD -> {
                ussdLayout.visibility = visibility
            }
            PaymentMethod.PHONE_NUMBER -> {
                phoneNumberLayout.visibility = VISIBLE
            }
            else -> {
                payWithOtherMethodsButton.visibility = INVISIBLE
            }
        }
    }

    private fun setupEventListener() {
        payWithSameCardButton.setOnClickListener {
            eventListener?.onPayWithSameCard()
        }

        payWithAnotherCardButton.setOnClickListener {
            eventListener?.onPayWithAnotherCard()
        }

        payWithTransferButton.setOnClickListener {
            eventListener?.onPayWithTransfer()
        }

        payWithUssdButton.setOnClickListener {
            eventListener?.onPayWithUssd()
        }

        payWithBankButton.setOnClickListener {
            eventListener?.onPayWithBank()
        }

        payWithPhoneNumberButton.setOnClickListener {
            eventListener?.onPayWithPhoneNumber()
        }

        payWithOtherMethodsButton.setOnClickListener {
            eventListener?.onPayWithOtherMethods()
        }

    }
}