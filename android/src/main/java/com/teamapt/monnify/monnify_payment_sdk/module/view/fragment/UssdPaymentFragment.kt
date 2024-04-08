package com.teamapt.monnify.monnify_payment_sdk.module.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teamapt.monnify.pos.util.ViewModelConstructor
import com.teamapt.monnify.pos.util.ViewModelFactory
import com.monnify.monnify_payment_sdk.R
import com.teamapt.monnify.monnify_payment_sdk.module.vm.UssdPaymentViewModel
import com.teamapt.monnify.monnify_payment_sdk.util.BanksProvider
import com.teamapt.monnify.monnify_payment_sdk.util.Constants

class UssdPaymentFragment : BaseFragment() {

    private lateinit var ussdPaymentViewModel: UssdPaymentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        ussdPaymentViewModel.transactionReference =
            if (arguments != null) requireArguments().getString(Constants.ARG_TRANSACTION_REF)!! else ""

        ussdPaymentViewModel.init(
            Bundle(),
            getActivityAsSdkActivity(),
            getNavigator(),
            getActivityAsSdkActivity()
        )

        return inflater.inflate(R.layout.plugin_fragment_ussd_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getNavigator().openUssdPaymentSelectBankFragment(this)
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        ussdPaymentViewModel = ViewModelProvider(
            this,
            ViewModelFactory.build(object : ViewModelConstructor {
                override fun create(): ViewModel {
                    return UssdPaymentViewModel(BanksProvider(requireActivity()))
                }
            })
        ).get(UssdPaymentViewModel::class.java)
    }
}