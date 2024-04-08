package com.monnify.monnify_payment_sdk.module.view.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monnify.monnify_payment_sdk.util.ViewModelConstructor
import com.monnify.monnify_payment_sdk.util.ViewModelFactory
import com.monnify.monnify_payment_sdk.R
import com.monnify.monnify_payment_sdk.customview.MonnifyDropDownView
import com.monnify.monnify_payment_sdk.customview.MonnifyProgressBar
import com.monnify.monnify_payment_sdk.customview.MonnifyRoundedOrangeGradientButton
import com.monnify.monnify_payment_sdk.customview.MonnifyRoundedOrangeGradientButton.ButtonState
import com.monnify.monnify_payment_sdk.customview.MonnifyWarningTextView
import com.monnify.monnify_payment_sdk.module.vm.TransferPaymentViewModel
import com.monnify.monnify_payment_sdk.rest.data.response.Bank
import com.monnify.monnify_payment_sdk.util.BanksProvider
import com.monnify.monnify_payment_sdk.util.Constants
import com.monnify.monnify_payment_sdk.util.Logger


class TransferPaymentFragment : BaseFragment() {

    private lateinit var transferPaymentViewModel: TransferPaymentViewModel

    private lateinit var accountNumberTextView: AppCompatTextView
    private lateinit var accountNumberCopiedTextView: AppCompatTextView
    private lateinit var bankNameTextView: AppCompatTextView
    private lateinit var merchantNameTextView: AppCompatTextView
    private lateinit var getBankUSSDButton: AppCompatButton
    private lateinit var banksDropDownView: MonnifyDropDownView<Bank>
    private lateinit var copyIconView: AppCompatImageView
    private lateinit var clickToCopyCode: AppCompatTextView
    private lateinit var shortCodeTextView: AppCompatTextView

    private lateinit var tapToCopyLayout: LinearLayoutCompat
    private lateinit var bankUSSDCodeDetailsLayout: LinearLayoutCompat
    private lateinit var dialCodeGroupLayout: LinearLayoutCompat
    private lateinit var continueButton: MonnifyRoundedOrangeGradientButton
    private lateinit var accountDurationCountDownTimer: MonnifyProgressBar
    private lateinit var warningTextView: MonnifyWarningTextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        transferPaymentViewModel.transactionReference =
            if (arguments != null) requireArguments().getString(Constants.ARG_TRANSACTION_REF)!! else ""

        transferPaymentViewModel.init(
            Bundle(),
            getActivityAsSdkActivity(),
            getNavigator(),
            getActivityAsSdkActivity()
        )

        return inflater.inflate(R.layout.plugin_fragment_transfer_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView(view)
        setupView()
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        transferPaymentViewModel = ViewModelProvider(
            this,
            ViewModelFactory.build(object : ViewModelConstructor {
                override fun create(): ViewModel {
                    return TransferPaymentViewModel(BanksProvider(requireActivity()))
                }
            })
        ).get(TransferPaymentViewModel::class.java)

        transferPaymentViewModel.liveBankTransferResponse.observe(this) {

            accountNumberTextView.text = it!!.accountNumber
            bankNameTextView.text = it.bankName
            merchantNameTextView.text = it.accountName
            getActivityAsSdkActivity().setTotalPayable(it.totalPayable)
            getActivityAsSdkActivity().setAmount(it.amount)
            getActivityAsSdkActivity().setPaymentFee(it.fee)

            accountDurationCountDownTimer.startCountDown(
                it.accountDurationSeconds!!.toLong() * 1000,
                transferPaymentViewModel.countDownCompleteLambda
            )
        }

        transferPaymentViewModel.liveGetAllBanksResponse.observe(this) {
            populateBanksDropDownView(it ?: emptyList())
        }

        transferPaymentViewModel.liveAccountDurationOver.observe(this) {
            if (it!!) {
                continueButton.state = ButtonState.LOADING
                continueButton.text = getString(R.string.checking_for_payment)
            }
        }

        transferPaymentViewModel.liveError.observe(this) { event ->
            event?.getContentIfNotHandled()?.let {
                errorTextView?.setTextAndMakeVisibleWithTimeout(it, secondsRemoveAfter = 60)
            }
        }

        transferPaymentViewModel.activeListeningState.observe(this) {
            if (it == false) {
                errorTextView?.setTextAndMakeVisibleWithTimeout(
                    getString(R.string.stomp_disconnect),
                    100
                )
            } else {
                errorTextView?.removeErrorView()
            }
        }

        transferPaymentViewModel.liveVerifyingTransaction.observe(this) {
            continueButton.state =
                if (it) ButtonState.LOADING
                else ButtonState.ENABLED

            continueButton.text =
                if (it) getString(R.string.checking_for_payment)
                else getString(R.string.transferred_money)

        }
    }

    private fun initView(view: View) {
        accountNumberTextView = view.findViewById(R.id.accountNumberTextView)
        accountNumberCopiedTextView = view.findViewById(R.id.accountNumberCopiedTextView)
        bankNameTextView = view.findViewById(R.id.bankNameTextView)
        merchantNameTextView = view.findViewById(R.id.merchantNameTextView)
        getBankUSSDButton = view.findViewById(R.id.getBankUSSDCode)
        banksDropDownView = view.findViewById(R.id.banksDropDownView)
        copyIconView = view.findViewById(R.id.accountNumberCopyIcon)
        clickToCopyCode = view.findViewById(R.id.clickToCopyCodeTextView)
        shortCodeTextView = view.findViewById(R.id.shortCodeTextView)
        tapToCopyLayout = view.findViewById(R.id.tapToCopyLayout)
        bankUSSDCodeDetailsLayout = view.findViewById(R.id.bankUSSDCodeDetailsLayout)
        dialCodeGroupLayout = view.findViewById(R.id.dialCodeGroupLayout)
        continueButton = view.findViewById(R.id.continueButton)
        accountDurationCountDownTimer = view.findViewById(R.id.transactionCountdownProgressBar)

        errorTextView = view.findViewById(R.id.monnifyErrorTextView)
        warningTextView = view.findViewById(R.id.monnifyWarningTextView)
    }

    private fun setupView() {
        warningTextView.setTextAndMakeVisible(getString(R.string.please_do_not_save_this_account_number))

        getBankUSSDButton.setOnClickListener {
            getBankUSSDButton.visibility = GONE
            bankUSSDCodeDetailsLayout.visibility = VISIBLE

            warningTextView.removeWaringView()
        }

        accountNumberTextView.setOnClickListener {
            copyDisplayedAccountNumber()
        }

        tapToCopyLayout.setOnClickListener {
            dialDisplayedUSSDCode()
        }

        clickToCopyCode.setOnClickListener {
            copyDisplayedUSSDCode()
        }

        continueButton.setOnClickListener {
            transferPaymentViewModel.verifyTransaction(shouldComplete = false)
        }

    }

    private fun populateBanksDropDownView(banks: List<Bank>) {

        val hint = Bank(name = getString(R.string.select_bank))

        val trimmedAmount =
            transferPaymentViewModel.initializeTransferPaymentResponse.totalPayable.toInt()

        banksDropDownView.setupView(getString(R.string.select_bank), banks, hint,
            object : MonnifyDropDownView.OnDropdownItemSelectedListener<Bank> {
                override fun onDropdownItemSelected(selectedItem: Bank?) {
                    val ussdCode = (selectedItem?.ussdTemplate ?: "")
                        .replace(
                            "Amount", trimmedAmount.toString()
                        )
                        .replace(
                            "AccountNumber",
                            transferPaymentViewModel.initializeTransferPaymentResponse.accountNumber.toString()
                        )
                    shortCodeTextView.text = ussdCode
                    dialCodeGroupLayout.visibility = VISIBLE
                }
            })
    }

    private fun copyDisplayedUSSDCode() {
        val ussdCode = shortCodeTextView.text

        val clipboard =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("ussdcode", ussdCode)
        clipboard?.setPrimaryClip(clip)

        clickToCopyCode.text = getString(R.string.copied)

        Handler(Looper.getMainLooper()).postDelayed({
            clickToCopyCode.text = getString(R.string.click_to_copy_code)
        }, 1000)
    }

    private fun copyDisplayedAccountNumber() {
        val accountNumber = accountNumberTextView.text

        val clipboard =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("account_number", accountNumber)
        clipboard?.setPrimaryClip(clip)

        accountNumberCopiedTextView.visibility = VISIBLE
        copyIconView.visibility = GONE

        Handler(Looper.getMainLooper()).postDelayed({
            accountNumberCopiedTextView.visibility = GONE
            copyIconView.visibility = VISIBLE
        }, 1000)
    }

    private fun dialDisplayedUSSDCode() {
        try {
            val ussdCode = shortCodeTextView.text

            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + Uri.encode(ussdCode.toString()))
            startActivity(intent)
        } catch (e: Exception) {
            Logger.log(this, e.message, Logger.LEVEL.ERROR)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        transferPaymentViewModel.stopListening()
    }

}