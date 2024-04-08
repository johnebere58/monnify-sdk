package com.teamapt.monnify.sdk.module.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.ViewModelProvider
import com.monnify.monnify_payment_sdk.R
import com.teamapt.monnify.sdk.module.vm.CardPaymentViewModel
import com.teamapt.monnify.sdk.util.Constants

class CardPaymentFragment : BaseFragment() {

    val rootView: ViewGroup
        get() = _rootView

    private lateinit var cardPaymentViewModel: CardPaymentViewModel

    private lateinit var _rootView: LinearLayoutCompat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cardPaymentViewModel.transactionReference =
            if (arguments != null) requireArguments().getString(Constants.ARG_TRANSACTION_REF)!! else ""

        cardPaymentViewModel.init(
            Bundle(),
            getActivityAsSdkActivity(),
            getNavigator(),
            getActivityAsSdkActivity()
        )

        return inflater.inflate(R.layout.plugin_fragment_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _rootView = view.findViewById(R.id.rootView)

        val useSavedCard: Boolean =
            requireArguments().getBoolean(Constants.ARG_USE_SAVED_CARD, false)

        getNavigator().openCardPaymentDetailsFragment(this, useSavedCard = useSavedCard)
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        cardPaymentViewModel =
            ViewModelProvider(this).get(CardPaymentViewModel::class.java)

        cardPaymentViewModel.liveAmountPayable.observe(this) {
            getActivityAsSdkActivity().setTotalPayable(it!!)
        }
        cardPaymentViewModel.liveAmount.observe(this) {
            getActivityAsSdkActivity().setAmount(it!!)
        }
        cardPaymentViewModel.livePaymentFee.observe(this) {
            getActivityAsSdkActivity().setPaymentFee(it!!)
        }
    }

}
