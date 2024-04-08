package com.teamapt.monnify.sdk.module

import androidx.fragment.app.Fragment
import com.teamapt.monnify.sdk.data.model.PaymentMethod
import com.teamapt.monnify.sdk.data.model.TransactionStatusDetails

interface IAppNavigator {
    fun openMainFragment()
    fun reopenMainFragment(transactionReference: String)
    fun openChangePaymentMethodFragment(transactionReference: String)
    fun openUssdPaymentFragment(transactionReference: String)
    fun openUssdPaymentSelectBankFragment(fragment: Fragment)
    fun openUssdPaymentDetailsFragment(fragment: Fragment)
    fun openCardPaymentFragment(transactionReference: String, useSavedCard: Boolean = false)
    fun openCardPaymentDetailsFragment(fragment: Fragment, useSavedCard: Boolean = false)
    fun openCardOtpFragment(fragment: Fragment)
    fun open3DsAuthFragment(fragment: Fragment)
    fun openTransferPaymentFragment(transactionReference: String)
    fun openPhonePaymentFragment(transactionReference: String)
    fun openPhonePaymentDetailsFragment(fragment: Fragment)
    fun openPhonePaymentAuthorizationFragment(fragment: Fragment)
    fun openBankPaymentFragment(transactionReference: String)
    fun openBankPaymentSelectBankFragment(fragment: Fragment)
    fun openBankPaymentDetailsFragment(fragment: Fragment)
    fun openBankPaymentOtpFragment(fragment: Fragment)
    fun openTransactionStatusFragment(transactionStatusDetails: TransactionStatusDetails)

    fun goBackOnce()
}
