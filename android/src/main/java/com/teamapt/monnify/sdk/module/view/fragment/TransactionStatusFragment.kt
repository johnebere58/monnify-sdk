package com.teamapt.monnify.sdk.module.view.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProvider
import com.teamapt.monnify.sdk.Monnify
import com.monnify.monnify_payment_sdk.R
import com.teamapt.monnify.sdk.Status
import com.teamapt.monnify.sdk.customview.MonnifyTransactionStatusButton
import com.teamapt.monnify.sdk.data.model.PaymentMethod
import com.teamapt.monnify.sdk.data.model.TransactionStatusDetails
import com.teamapt.monnify.sdk.module.vm.PaymentMethodsViewModel
import com.teamapt.monnify.sdk.util.Constants
import com.teamapt.monnify.sdk.util.Logger
import com.teamapt.monnify.sdk.util.MoneyFormatter
import java.math.BigDecimal


class TransactionStatusFragment : BaseFragment() {

    private val countdownTime = 10000L

    private lateinit var paymentMethodsViewModel: PaymentMethodsViewModel

    private lateinit var transactionStatusIcon: AppCompatImageView
    private lateinit var transactionStatusTextView: AppCompatTextView
    private lateinit var transactionStatusMessageTextView: AppCompatTextView

    private lateinit var transactionStatusButton: MonnifyTransactionStatusButton

    private lateinit var transactionStatusDetails: TransactionStatusDetails

    private var countDownTimer: CountDownTimer? = null

    private lateinit var returningToMerchantTextView: AppCompatTextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (arguments != null)
            transactionStatusDetails =
                arguments?.getParcelable(Constants.ARG_TRANSACTION_STATUS_DETAILS)
                    ?: TransactionStatusDetails()

        return inflater.inflate(
            R.layout.plugin_fragment_transaction_status_fragment,
            container,
            false
        )
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        paymentMethodsViewModel =
            ViewModelProvider(requireActivity()).get(PaymentMethodsViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Logger.log(
            this,
            "TransactionStatusFragment onViewCreated was called and it should show $transactionStatusDetails"
        )

        transactionStatusIcon = view.findViewById(R.id.transactionStatusIcon)
        transactionStatusTextView = view.findViewById(R.id.transactionStatusTextView)
        transactionStatusMessageTextView = view.findViewById(R.id.transactionStatusMessageTextView)
        transactionStatusButton = view.findViewById(R.id.monnifyTransactionStatusButton)

        returningToMerchantTextView = view.findViewById(R.id.returningToMerchantTextView)

        setPropertiesToViews()
    }

    private fun setPropertiesToViews() {

        bindTransactionStatusDetailsToPage()
        updateMonnifyPaymentStatus()

        setupTransactionStatusButton()

        getActivityAsSdkActivity().setActivityResult()
    }

    private fun bindTransactionStatusDetailsToPage() {

        val amount: BigDecimal =
            if (transactionStatusDetails.amountPayable.compareTo(BigDecimal.ZERO) == 0)
                transactionStatusDetails.amountPaid
            else
                transactionStatusDetails.amountPayable

        val currencyCode = transactionStatusDetails.currencyCode ?: ""

        val paymentMethod: PaymentMethod? = paymentMethodsViewModel.getCurrentPaymentMethod()

        val formattedAmountPaid = MoneyFormatter.format(
            amount = amount,
            currencyCode = currencyCode
        )

        var status = ""
        @DrawableRes var icon = 0
        var statusMessage = ""

        when (transactionStatusDetails.status) {
            Status.PAID -> {
                icon = R.drawable.ic_transaction_status_success
                status = getString(R.string.transaction_status, "Success")
                statusMessage = getString(
                    R.string.transaction_status_message,
                    formattedAmountPaid,
                    "successful"
                )
                transactionStatusButton.setupView()

                startCountDownToReturnToMerchant(countdownTime)
            }
            Status.OVERPAID,
            Status.PARTIALLY_PAID,
            Status.PENDING -> {
                icon = R.drawable.ic_transaction_status_warning
                status = getString(R.string.transaction_status, "Success")
                statusMessage = getString(
                    R.string.transaction_status_message,
                    formattedAmountPaid,
                    "successful"
                )
                transactionStatusButton.setupView()

                startCountDownToReturnToMerchant(countdownTime)
            }
            Status.FAILED,
            Status.PAYMENT_GATEWAY_ERROR -> {
                icon = R.drawable.ic_transaction_status_failed
                status = getString(R.string.transaction_status, "Failed")
                statusMessage = getString(
                    R.string.transaction_status_message,
                    formattedAmountPaid,
                    "unsuccessful"
                )
                transactionStatusButton.setupView(paymentMethod)
            }
            Status.CANCELLED -> {
                icon = R.drawable.ic_transaction_status_failed
                statusMessage = transactionStatusDetails.message ?: ""
                transactionStatusButton.setupView()

                startCountDownToReturnToMerchant(countdownTime)
            }
            else -> {}
        }

        transactionStatusIcon.setImageResource(icon)
        transactionStatusTextView.text = status
        transactionStatusMessageTextView.text = statusMessage
    }

    private fun setupTransactionStatusButton() {
        val transactionReference = transactionStatusDetails.transactionReference

        transactionStatusButton.eventListener =
            object : MonnifyTransactionStatusButton.MonnifyTransactionStatusButtonClickListener {
                override fun onPayWithSameCard() {
                    getNavigator().openCardPaymentFragment(
                        transactionReference = transactionReference,
                        useSavedCard = true
                    )
                }

                override fun onPayWithAnotherCard() {
                    getNavigator().openCardPaymentFragment(transactionReference = transactionReference)
                }

                override fun onPayWithTransfer() {
                    getNavigator().openTransferPaymentFragment(transactionReference = transactionReference)
                }

                override fun onPayWithUssd() {
                    getNavigator().openUssdPaymentFragment(transactionReference = transactionReference)
                }

                override fun onPayWithBank() {
                    getNavigator().openBankPaymentFragment(transactionReference = transactionReference)
                }

                override fun onPayWithPhoneNumber() {
                    getNavigator().openPhonePaymentFragment(transactionReference = transactionReference)
                }

                override fun onPayWithOtherMethods() {
                    getNavigator().reopenMainFragment(transactionReference = transactionReference)
                }
            }
    }

    private fun updateMonnifyPaymentStatus() {
        Monnify.instance.paymentStatus.status = transactionStatusDetails.status
    }

    private fun startCountDownToReturnToMerchant(periodInMillis: Long) {
        Logger.log(this, "startCountDownToReturnToMerchant was called $periodInMillis")

        returningToMerchantTextView.visibility = VISIBLE

        countDownTimer = object : CountDownTimer(periodInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                returningToMerchantTextView.text = getString(
                    R.string.returning_to_merchant_in_some_seconds,
                    millisUntilFinished / 1000
                )
            }

            override fun onFinish() {
                Logger.log(
                    this,
                    "startCountDownToReturnToMerchant onFinish was called $periodInMillis"
                )

                getNavigator().goBackOnce()
            }
        }

        (countDownTimer as CountDownTimer).start()

    }

    override fun onDetach() {
        countDownTimer?.cancel()
        super.onDetach()
    }
}

