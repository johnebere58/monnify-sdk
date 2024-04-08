package com.monnify.monnify_payment_sdk.module.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProvider
import com.monnify.monnify_payment_sdk.R
import com.monnify.monnify_payment_sdk.customview.MonnifyEditText
import com.monnify.monnify_payment_sdk.customview.MonnifyRoundedOrangeGradientButton
import com.monnify.monnify_payment_sdk.module.vm.CardPaymentViewModel

class CardPaymentOtpFragment : BaseFragment() {

    private lateinit var cardPaymentViewModel: CardPaymentViewModel


    private lateinit var otpEditText: MonnifyEditText
    private lateinit var continueButton: MonnifyRoundedOrangeGradientButton
    private lateinit var otpDescriptionTextView: AppCompatTextView
    private lateinit var goBackButton: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.plugin_fragment_card_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        otpEditText = view.findViewById(R.id.otpEditText)
        continueButton = view.findViewById(R.id.continueButton)
        otpDescriptionTextView = view.findViewById(R.id.otpDescriptionTextView)

        goBackButton = view.findViewById(R.id.goBackButton)

        errorTextView = view.findViewById(R.id.monnifyErrorTextView)

        setPropertiesToView()
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        cardPaymentViewModel =
            ViewModelProvider(requireParentFragment()).get(CardPaymentViewModel::class.java)

        cardPaymentViewModel.liveOtpValidation.observe(this) {
            continueButton.state =
                if (it) MonnifyRoundedOrangeGradientButton.ButtonState.ENABLED
                else MonnifyRoundedOrangeGradientButton.ButtonState.DISABLED
        }

        cardPaymentViewModel.liveCardOtpAuthorizationError.observe(this) {
            errorTextView?.setTextAndMakeVisible(it)
            otpEditText.clear()
        }

        cardPaymentViewModel.liveError.observe(this) { event ->
            event?.getContentIfNotHandled()?.let {
                errorTextView?.setTextAndMakeVisible(it)
            }
        }
    }

    private fun setPropertiesToView() {
        otpDescriptionTextView.text = buildOtpDescriptionText()

        continueButton.setOnClickListener {
            cardPaymentViewModel.authorizeCardOtp()
        }

        goBackButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        addInputEventListener()
    }

    private fun addInputEventListener() {
        otpEditText.eventListener = object : MonnifyEditText.MonnifyEditTextInputEventListener {
            override fun onViewLoseFocus() {
            }

            override fun onTextChanged(text: CharSequence) {
                cardPaymentViewModel.setOtp(text.toString())
                cardPaymentViewModel.shouldAllowOtpValidation(otpEditText.validate())
            }

            override fun onValidate(text: CharSequence?): Boolean {
                return text?.length ?: 0 >= 4
            }
        }
    }


    private fun buildOtpDescriptionText(): CharSequence {
        return (cardPaymentViewModel.cardPaymentResponse.responseDescription
            ?: getString(R.string.an_otp_has_been_sent_phone_number)) + "\n" + getString(R.string.enter_it_to_complete_this_payment)
    }
}