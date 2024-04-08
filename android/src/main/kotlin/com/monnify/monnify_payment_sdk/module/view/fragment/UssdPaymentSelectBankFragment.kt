package com.monnify.monnify_payment_sdk.module.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teamapt.monnify.pos.util.ViewModelConstructor
import com.teamapt.monnify.pos.util.ViewModelFactory
import com.monnify.monnify_payment_sdk.R
import com.monnify.monnify_payment_sdk.customview.MonnifyDropDownView
import com.monnify.monnify_payment_sdk.module.vm.UssdPaymentViewModel
import com.monnify.monnify_payment_sdk.rest.data.response.Bank
import com.monnify.monnify_payment_sdk.util.BanksProvider

class UssdPaymentSelectBankFragment : BaseFragment() {

    private lateinit var ussdPaymentViewModel: UssdPaymentViewModel

    private lateinit var banksDropDownView: MonnifyDropDownView<Bank>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.plugin_fragment_ussd_payment_select_bank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView(view)
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        ussdPaymentViewModel = ViewModelProvider(
            requireParentFragment(),
            ViewModelFactory.build(object : ViewModelConstructor {
                override fun create(): ViewModel {
                    return UssdPaymentViewModel(BanksProvider(requireActivity()))
                }
            })
        ).get(UssdPaymentViewModel::class.java)

        ussdPaymentViewModel.liveGetAllBanksResponse.observe(this) {
            populateBanksDropDownView(it ?: emptyList())
        }

        ussdPaymentViewModel.liveGetSelectedBank.observe(this) {
            it.getContentIfNotHandled().let {
                getNavigator().openUssdPaymentDetailsFragment(requireParentFragment())
            }
        }

        ussdPaymentViewModel.liveError.observe(this) { event ->
            event?.getContentIfNotHandled()?.let {
                errorTextView?.setTextAndMakeVisible(it)
            }
        }

        ussdPaymentViewModel.activeListeningState.observe(this) {
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

        val hint = Bank(name = getString(R.string.select_your_bank))

        banksDropDownView.setupView(
            getString(R.string.select_bank),
            banks,
            hint,
            object : MonnifyDropDownView.OnDropdownItemSelectedListener<Bank> {
                override fun onDropdownItemSelected(selectedItem: Bank?) {
                    val bankUssdCode = Regex("(\\d+)")
                        .findAll(selectedItem?.ussdTemplate ?: "")
                        .map { it.value }
                        .first()


                    ussdPaymentViewModel.initializeUssdPayment(bankUssdCode = bankUssdCode)

                    ussdPaymentViewModel.onBankSelected(selectedItem ?: Bank())
                }
            })
    }

    private fun initView(view: View) {
        banksDropDownView = view.findViewById(R.id.banksDropDownView)
        errorTextView = view.findViewById(R.id.monnifyErrorTextView)
    }

    override fun onResume() {
        super.onResume()
        ussdPaymentViewModel.loadAllBanks()
    }
}