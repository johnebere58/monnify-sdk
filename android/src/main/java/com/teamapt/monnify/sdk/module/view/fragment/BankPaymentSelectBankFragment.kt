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
import com.teamapt.monnify.sdk.customview.MonnifyDropDownView
import com.teamapt.monnify.sdk.module.vm.BankPaymentViewModel
import com.teamapt.monnify.sdk.rest.data.response.Bank
import com.teamapt.monnify.sdk.util.BanksProvider
import com.teamapt.monnify.sdk.util.Logger

class BankPaymentSelectBankFragment : BaseFragment() {

    private lateinit var bankPaymentViewModel: BankPaymentViewModel

    private lateinit var banksDropDownView: MonnifyDropDownView<Bank>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.plugin_fragment_bank_payment_select_bank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        banksDropDownView = view.findViewById(R.id.banksDropDownView)

        errorTextView = view.findViewById(R.id.monnifyErrorTextView)
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        bankPaymentViewModel =
            ViewModelProvider(
                requireParentFragment(),
                ViewModelFactory.build(object : ViewModelConstructor {
                    override fun create(): ViewModel {
                        return BankPaymentViewModel(BanksProvider(requireActivity()))
                    }
                })
            ).get(BankPaymentViewModel::class.java)

        bankPaymentViewModel.liveGetBankPaymentBanksResponse.observe(this) {
            populateBanksDropDownView(it)
        }

        bankPaymentViewModel.liveEventOnBankSelected.observe(this) {
            bankPaymentViewModel.initializeBankPayment()
        }

        bankPaymentViewModel.liveInitializeBankPaymentResponse.observe(this) {
            it.getContentIfNotHandled().let {
                getNavigator().openBankPaymentDetailsFragment(requireParentFragment())
            }
        }

        bankPaymentViewModel.liveError.observe(this) { event ->
            event?.getContentIfNotHandled()?.let {
                errorTextView?.setTextAndMakeVisibleWithTimeout(it)
            }
        }

        bankPaymentViewModel.activeListeningState.observe(this) {
            if (it == false) {
                errorTextView?.setTextAndMakeVisibleWithTimeout(
                    getString(R.string.stomp_disconnect),
                    100
                )
            } else {
                errorTextView?.removeErrorView()
            }
        }
    }


    private fun populateBanksDropDownView(banks: List<Bank>) {

        val hint = Bank(name = getString(R.string.select_bank))

        banksDropDownView.setupView(getString(R.string.select_bank), banks, hint,
            object : MonnifyDropDownView.OnDropdownItemSelectedListener<Bank> {
                override fun onDropdownItemSelected(selectedItem: Bank?) {
                    bankPaymentViewModel.setSelectedBank(selectedItem ?: Bank())
                }
            })
    }

    override fun onResume() {
        super.onResume()
        Logger.log(this, "OnResume called")
        bankPaymentViewModel.loadBanks()
        bankPaymentViewModel.resetState()
    }
}