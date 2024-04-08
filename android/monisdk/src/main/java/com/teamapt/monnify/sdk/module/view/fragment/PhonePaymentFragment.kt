package com.teamapt.monnify.sdk.module.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.teamapt.monnify.sdk.R
import com.teamapt.monnify.sdk.module.vm.PaymentMethodsViewModel
import com.teamapt.monnify.sdk.module.vm.PhonePaymentViewModel
import com.teamapt.monnify.sdk.util.Constants
import java.math.BigDecimal

class PhonePaymentFragment : BaseFragment() {

    private lateinit var phonePaymentViewModel: PhonePaymentViewModel
    private lateinit var paymentMethodViewModel: PaymentMethodsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        phonePaymentViewModel.transactionReference =
            if (arguments != null) requireArguments().getString(Constants.ARG_TRANSACTION_REF)!! else ""

        phonePaymentViewModel.init(
            Bundle(),
            getActivityAsSdkActivity(),
            getNavigator(),
            getActivityAsSdkActivity()
        )

        return inflater.inflate(R.layout.plugin_fragment_phone_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getNavigator().openPhonePaymentDetailsFragment(this)
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        phonePaymentViewModel = ViewModelProvider(this).get(PhonePaymentViewModel::class.java)
        paymentMethodViewModel =
            ViewModelProvider(requireActivity()).get(PaymentMethodsViewModel::class.java)

        phonePaymentViewModel.liveEventHasInitializePayment.observe(this) {

            getActivityAsSdkActivity().setTotalPayable(
                paymentMethodViewModel.liveAmountBeingPaid.value ?: BigDecimal.ZERO
            )
            getActivityAsSdkActivity().setAmount(
                paymentMethodViewModel.liveAmountBeingPaid.value ?: BigDecimal.ZERO
            )
            getActivityAsSdkActivity().setPaymentFee(BigDecimal.ZERO)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        phonePaymentViewModel.stopListening()
    }
}