package com.teamapt.monnify.sdk.module.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.teamapt.monnify.sdk.R
import com.teamapt.monnify.sdk.customview.MonnifyEditText
import com.teamapt.monnify.sdk.customview.MonnifyRoundedOrangeGradientButton
import com.teamapt.monnify.sdk.module.vm.PhonePaymentViewModel
import com.teamapt.monnify.sdk.util.Logger

class PhonePaymentDetailsFragment : BaseFragment() {


    private lateinit var monnifyEditText: MonnifyEditText
    private lateinit var continueButton: MonnifyRoundedOrangeGradientButton

    private lateinit var phonePaymentViewModel: PhonePaymentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.plugin_fragment_phone_payment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        monnifyEditText = view.findViewById(R.id.monnifyEditText)
        continueButton = view.findViewById(R.id.continueButton)

        errorTextView = view.findViewById(R.id.monnifyErrorTextView)

        setupViewProperties()
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        phonePaymentViewModel =
            ViewModelProvider(requireParentFragment()).get(PhonePaymentViewModel::class.java)

        phonePaymentViewModel.liveError.observe(this) { event ->
            event?.getContentIfNotHandled()?.let {
                errorTextView?.setTextAndMakeVisible(it)
            }
        }

        phonePaymentViewModel.activeListeningState.observe(this) {
            if (it == false) {
                errorTextView?.setTextAndMakeVisibleWithTimeout(
                    getString(R.string.stomp_disconnect),
                    100
                )
            } else {
                errorTextView?.removeErrorView()
            }
        }

        phonePaymentViewModel.liveActivateContinueButton.observe(this) {
            continueButton.state =
                if (it)
                    MonnifyRoundedOrangeGradientButton.ButtonState.ENABLED
                else
                    MonnifyRoundedOrangeGradientButton.ButtonState.DISABLED
        }
    }

    private fun setupViewProperties() {
        addEventListenerToEditText()

        continueButton.setOnClickListener {
            getNavigator().openPhonePaymentAuthorizationFragment(requireParentFragment())
        }
    }

    private fun addEventListenerToEditText() {
        monnifyEditText.eventListener = object : MonnifyEditText.MonnifyEditTextInputEventListener {
            override fun onViewLoseFocus() {
                Logger.log(this, "monnifyEditText onViewLoseFocus called")
            }

            override fun onTextChanged(text: CharSequence) {
                phonePaymentViewModel.setPhoneNumber(text.toString())
                phonePaymentViewModel.activateContinueButton(monnifyEditText.validate())
            }

            override fun onValidate(text: CharSequence?): Boolean {
                Logger.log(this, "monnifyEditText onValidate called")
                return text?.length ?: 0 == 11
            }

        }
    }
}