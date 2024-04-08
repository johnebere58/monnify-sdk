package com.teamapt.monnify.monnify_payment_sdk.module.view.fragment

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.monnify.monnify_payment_sdk.R
import com.teamapt.monnify.monnify_payment_sdk.customview.MonnifyErrorTextView
import com.teamapt.monnify.monnify_payment_sdk.customview.MonnifyPaymentMethodHeaderView
import com.teamapt.monnify.monnify_payment_sdk.module.IAppNavigator
import com.teamapt.monnify.monnify_payment_sdk.module.view.activity.SdkActivity
import com.teamapt.monnify.monnify_payment_sdk.module.vm.PaymentMethodsViewModel

abstract class BaseFragment : androidx.fragment.app.Fragment() {

    private var listener: OnFragmentInteractionListener? = null

    protected var errorTextView: MonnifyErrorTextView? = null

    private var paymentMethodHeaderView: MonnifyPaymentMethodHeaderView? = null

    private lateinit var paymentMethodsViewModel: PaymentMethodsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeToViewModel()
    }

    protected open fun subscribeToViewModel() {
        getActivityAsSdkActivity().setActivityResult()

        paymentMethodsViewModel =
            ViewModelProvider(requireActivity()).get(PaymentMethodsViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorTextView = view.findViewById(R.id.monnifyErrorTextView)

//
//        if (Monnify.instance.getApplicationMode() == ApplicationMode.TEST) {
//
//            (view.findViewById(R.id.testModeIndicator) as AppCompatTextView).visibility =
//                View.VISIBLE
//        }


        paymentMethodHeaderView = view.findViewById(R.id.monnifyPaymentMethodHeader)

        activateChangePaymentMethodButton(paymentMethodHeaderView)
    }

    private fun activateChangePaymentMethodButton(paymentMethodHeaderView: MonnifyPaymentMethodHeaderView?) {
        if (paymentMethodHeaderView == null)
            return

        paymentMethodHeaderView.setOnChangeButtonVisibility(paymentMethodsViewModel.paymentMethodList.size > 0)

        paymentMethodHeaderView.setOnChangeButtonClickListener {
            getNavigator().openChangePaymentMethodFragment(transactionReference = paymentMethodsViewModel.transactionReference)
        }
    }

    protected fun dimen(@DimenRes resId: Int): Int {
        return resources.getDimension(resId).toInt()
    }

    protected fun color(@ColorRes resId: Int): Int {
        return ContextCompat.getColor(requireActivity().applicationContext, resId)
    }

    protected fun drawable(@DrawableRes resId: Int): Drawable {
        return ContextCompat.getDrawable(requireActivity().applicationContext, resId)!!
    }

    protected fun integer(@IntegerRes resId: Int): Int {
        return resources.getInteger(resId)
    }

    protected fun getNavigator(): IAppNavigator {
        return getActivityAsSdkActivity().navigator
    }

    protected fun getActivityAsSdkActivity(): SdkActivity {
        return activity as SdkActivity
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }
}