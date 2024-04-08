package com.monnify.monnify_payment_sdk.module.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.monnify.monnify_payment_sdk.R
import com.monnify.monnify_payment_sdk.customview.MonnifyPaymentMethodHeaderView
import com.monnify.monnify_payment_sdk.customview.NonScrollListView
import com.monnify.monnify_payment_sdk.data.PaymentMethodAdapter
import com.monnify.monnify_payment_sdk.data.model.PaymentMethod
import com.monnify.monnify_payment_sdk.module.vm.PaymentMethodsViewModel
import com.monnify.monnify_payment_sdk.util.Constants

class ChangePaymentMethodFragment : BaseFragment() {

    private lateinit var paymentMethodsViewModel: PaymentMethodsViewModel

    private lateinit var paymentMethodHeader: MonnifyPaymentMethodHeaderView
    private lateinit var paymentMethodsListView: NonScrollListView


    override fun subscribeToViewModel() {
        super.subscribeToViewModel()
        paymentMethodsViewModel =
            ViewModelProvider(requireActivity()).get(PaymentMethodsViewModel::class.java)
        // No need to call init() again on the view model as it has already been done in the SdkActivity

        paymentMethodsViewModel.liveError.observe(this) { event ->
            event?.getContentIfNotHandled()?.let {
                errorTextView?.setTextAndMakeVisibleWithTimeout(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (arguments != null)
            paymentMethodsViewModel.transactionReference =
                arguments?.getString(Constants.ARG_TRANSACTION_REF) ?: ""

        return inflater.inflate(R.layout.plugin_fragment_change_payment_method, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paymentMethodHeader = view.findViewById(R.id.monnifyPaymentMethodHeader)
        paymentMethodsListView = view.findViewById(R.id.paymentMethodListView)

        setupViews()
    }

    private fun setupViews() {

        val currentPaymentMethod = paymentMethodsViewModel.getCurrentPaymentMethod()

        paymentMethodHeader.text = getString(currentPaymentMethod?.title ?: 0)
        paymentMethodHeader.icon = currentPaymentMethod?.methodIcon ?: 0

        paymentMethodHeader.setOnChangeButtonClickListener {
//            requireActivity().supportFragmentManager.popBackStack()
            getActivityAsSdkActivity().onBackPressed()
        }

        paymentMethodsListView.adapter = PaymentMethodAdapter(
            requireActivity(),
            paymentMethodsViewModel.updatePaymentMethodsList(currentPaymentMethod) as List<PaymentMethod>
        )

        paymentMethodsListView.onItemClickListener = AdapterView.OnItemClickListener(
            function = fun(parent: AdapterView<*>, _: View, position: Int, _: Long) {
                doOnItemClick((parent.adapter.getItem(position) as PaymentMethod))
            }
        )
    }

    private fun doOnItemClick(paymentMethod: PaymentMethod) {
        paymentMethodsViewModel.setCurrentPaymentMethod(paymentMethod)

        when (paymentMethod) {
            PaymentMethod.CARD -> {
                getNavigator().openCardPaymentFragment(paymentMethodsViewModel.transactionReference)
            }

            PaymentMethod.ACCOUNT_TRANSFER -> {
                getNavigator().openTransferPaymentFragment(paymentMethodsViewModel.transactionReference)
            }

            PaymentMethod.DIRECT_DEBIT -> {
                getNavigator().openBankPaymentFragment(paymentMethodsViewModel.transactionReference)
            }

            PaymentMethod.PHONE_NUMBER -> {
                getNavigator().openPhonePaymentFragment(paymentMethodsViewModel.transactionReference)
            }

            PaymentMethod.USSD -> {
                getNavigator().openUssdPaymentFragment(paymentMethodsViewModel.transactionReference)
            }
            else -> {}
        }
    }
}