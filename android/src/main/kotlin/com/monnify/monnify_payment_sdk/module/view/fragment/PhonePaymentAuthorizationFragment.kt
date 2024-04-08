package com.monnify.monnify_payment_sdk.module.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.monnify.monnify_payment_sdk.R
import com.monnify.monnify_payment_sdk.customview.MonnifyNewtonCradleIndicator
import com.monnify.monnify_payment_sdk.customview.MonnifyRoundedOrangeGradientButton
import com.monnify.monnify_payment_sdk.module.vm.PhonePaymentViewModel

class PhonePaymentAuthorizationFragment : BaseFragment() {

    private lateinit var continueButton: MonnifyRoundedOrangeGradientButton
    private lateinit var cradleIndicator: MonnifyNewtonCradleIndicator

    private lateinit var phonePaymentViewModel: PhonePaymentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(
            R.layout.plugin_fragment_phone_payment_authorization,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        continueButton = view.findViewById(R.id.continueButton)
        cradleIndicator = view.findViewById(R.id.monnifyCradleIndicator)

        cradleIndicator.start()

        errorTextView = view.findViewById(R.id.monnifyErrorTextView)

        setupViewProperties()
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        phonePaymentViewModel =
            ViewModelProvider(requireParentFragment()).get(PhonePaymentViewModel::class.java)

        phonePaymentViewModel.initializePhoneNumberPayment()


        phonePaymentViewModel.liveError.observe(this) { event ->
            event?.getContentIfNotHandled()?.let {
                errorTextView?.setTextAndMakeVisible(it)
            }
        }

        phonePaymentViewModel.activeListeningState.observe(this) {
            if (it == false) {
                errorTextView?.setTextAndMakeVisibleWithTimeout(getString(R.string.stomp_disconnect), 100)
            } else {
                errorTextView?.removeErrorView()
            }
        }

        phonePaymentViewModel.liveVerifyingTransaction.observe(this) {

            continueButton.state =
                if (it) MonnifyRoundedOrangeGradientButton.ButtonState.LOADING
                else MonnifyRoundedOrangeGradientButton.ButtonState.ENABLED

            continueButton.text =
                if (it) getString(R.string.checking_for_payment)
                else getString(R.string.transferred_money)
        }
    }

    private fun setupViewProperties() {
        continueButton.setOnClickListener {
            phonePaymentViewModel.verifyTransaction(shouldComplete = false)
        }
    }
}