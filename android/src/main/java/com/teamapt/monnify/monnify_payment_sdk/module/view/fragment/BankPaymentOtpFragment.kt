package com.teamapt.monnify.monnify_payment_sdk.module.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProvider
import com.monnify.monnify_payment_sdk.R
import com.teamapt.monnify.monnify_payment_sdk.customview.MonnifyEditText
import com.teamapt.monnify.monnify_payment_sdk.customview.MonnifyRoundedOrangeGradientButton
import com.teamapt.monnify.monnify_payment_sdk.module.vm.BankPaymentViewModel

class BankPaymentOtpFragment : BaseFragment() {

    private lateinit var bankPaymentViewModel: BankPaymentViewModel

    private lateinit var otpEditText: MonnifyEditText
    private lateinit var otpDescriptionTextView: AppCompatTextView
    private lateinit var continueButton: MonnifyRoundedOrangeGradientButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.plugin_fragment_bank_payment_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        otpEditText = view.findViewById(R.id.otpEditText)
        otpDescriptionTextView = view.findViewById(R.id.otpDescriptionTextView)

        continueButton = view.findViewById(R.id.continueButton)

        setupView()
    }

    override fun subscribeToViewModel() {
        bankPaymentViewModel =
            ViewModelProvider(requireParentFragment()).get(BankPaymentViewModel::class.java)

        bankPaymentViewModel.liveEventActivateOtpAuthorizeButton.observe(this) {
            continueButton.state =
                if (it)
                    MonnifyRoundedOrangeGradientButton.ButtonState.ENABLED
                else
                    MonnifyRoundedOrangeGradientButton.ButtonState.DISABLED
        }

        bankPaymentViewModel.liveError.observe(this) { event ->
            event?.getContentIfNotHandled()?.let {
                errorTextView?.setTextAndMakeVisible(it)
            }
        }

        bankPaymentViewModel.activeListeningState.observe(this) {
            if (it == false) {
                errorTextView?.setTextAndMakeVisibleWithTimeout(
                    getString(R.string.stomp_disconnect),
                    120
                )
            } else {
                errorTextView?.removeErrorView()
            }
        }
    }

    private fun setupView() {
        otpDescriptionTextView.text = buildOtpDescriptionText()

        continueButton.setOnClickListener {
            bankPaymentViewModel.authorizePayment()
        }

        addEventListenerToEditText()
    }

    private fun addEventListenerToEditText() {
        otpEditText.eventListener = object : MonnifyEditText.MonnifyEditTextInputEventListener {
            override fun onViewLoseFocus() {

            }

            override fun onTextChanged(text: CharSequence) {
                bankPaymentViewModel.setOtp(text.toString())
                bankPaymentViewModel.shouldAuthorizePayment(otpEditText.validate())
            }

            override fun onValidate(text: CharSequence?): Boolean {
                return text?.length ?: 0 >= 4
            }

        }
    }

    private fun buildOtpDescriptionText(): CharSequence {
        return (bankPaymentViewModel.liveBankPaymentChargeAccount.value?.responseDescription
            ?: getString(R.string.an_otp_has_been_sent_phone_number)) + "\n" + getString(R.string.enter_it_to_complete_this_payment)
    }
}