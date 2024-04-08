package com.teamapt.monnify.sdk.module.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teamapt.monnify.pos.util.ViewModelConstructor
import com.teamapt.monnify.pos.util.ViewModelFactory
import com.monnify.monnify_payment_sdk.R
import com.teamapt.monnify.sdk.module.vm.BankPaymentViewModel
import com.teamapt.monnify.sdk.util.BanksProvider
import com.teamapt.monnify.sdk.util.Constants
import java.math.BigDecimal

class BankPaymentFragment : BaseFragment() {

    private lateinit var bankPaymentViewModel: BankPaymentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bankPaymentViewModel.transactionReference =
            if (arguments != null) requireArguments().getString(Constants.ARG_TRANSACTION_REF)!! else ""

        bankPaymentViewModel.init(
            Bundle(),
            getActivityAsSdkActivity(),
            getNavigator(),
            getActivityAsSdkActivity()
        )

        return inflater.inflate(R.layout.plugin_fragment_bank_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getNavigator().openBankPaymentSelectBankFragment(this)
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        bankPaymentViewModel =
            ViewModelProvider(this, ViewModelFactory.build(object : ViewModelConstructor {
                override fun create(): ViewModel {
                    return BankPaymentViewModel(BanksProvider(requireActivity()))
                }
            })).get(BankPaymentViewModel::class.java)



        bankPaymentViewModel.liveInitializeBankPaymentResponse.observe(this) { event ->
            event?.getContentIfNotHandled()?.let {
                getActivityAsSdkActivity().setTotalPayable(
                    it?.totalAmountPayable ?: BigDecimal.ZERO
                )
                getActivityAsSdkActivity().setAmount(it?.amount ?: BigDecimal.ZERO)
                getActivityAsSdkActivity().setPaymentFee(it?.paymentFee ?: BigDecimal.ZERO)
            }
        }
    }
}