package com.teamapt.monnify.sdk.module

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.teamapt.monnify.sdk.R
import com.teamapt.monnify.sdk.data.model.TransactionStatusDetails
import com.teamapt.monnify.sdk.module.view.activity.SdkActivity
import com.teamapt.monnify.sdk.module.view.fragment.*
import com.teamapt.monnify.sdk.util.Constants
import com.teamapt.monnify.sdk.util.DeferredFragmentTransaction
import com.teamapt.monnify.sdk.util.Logger

class AppNavigator(private val activity: SdkActivity) : IAppNavigator {

    override fun openMainFragment() {
        val fragmentManager = activity.supportFragmentManager
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, MainFragment())
        fragmentTransaction.addToBackStack(MainFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    override fun reopenMainFragment(transactionReference: String) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.ARG_TRANSACTION_REF, transactionReference)
        val mainFragment = MainFragment()
        mainFragment.arguments = bundle

        val fragmentManager = activity.supportFragmentManager

        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, mainFragment)
        fragmentTransaction.addToBackStack(MainFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    override fun openChangePaymentMethodFragment(transactionReference: String) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.ARG_TRANSACTION_REF, transactionReference)
        val changePaymentDetailsFragment = ChangePaymentMethodFragment()
        changePaymentDetailsFragment.arguments = bundle

        val fragmentManager = activity.supportFragmentManager

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragmentContainer, changePaymentDetailsFragment)
        fragmentTransaction.addToBackStack(ChangePaymentMethodFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    override fun openUssdPaymentFragment(transactionReference: String) {
        if (!activity.isInActiveState)
            return

        val bundle = Bundle()
        bundle.putString(Constants.ARG_TRANSACTION_REF, transactionReference)
        val ussdPaymentFragment = UssdPaymentFragment()
        ussdPaymentFragment.arguments = bundle

        val fragmentManager = activity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.fragmentContainer, ussdPaymentFragment)
        fragmentTransaction.addToBackStack(UssdPaymentFragment::class.java.simpleName)
        fragmentTransaction.setCustomAnimations(
            R.anim.com_monnify_sdk_slide_in_from_right, R.anim.com_monnify_sdk_slide_out_to_left,
            R.anim.com_monnify_sdk_slide_in_from_left, R.anim.com_monnify_sdk_slide_out_to_right
        )
        fragmentTransaction.commit()
    }

    override fun openUssdPaymentSelectBankFragment(fragment: Fragment) {
        if (!activity.isInActiveState)
            return

        val fragmentManager: FragmentManager = fragment.childFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.childFragmentContainer, UssdPaymentSelectBankFragment())
        fragmentTransaction.addToBackStack(UssdPaymentSelectBankFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    override fun openUssdPaymentDetailsFragment(fragment: Fragment) {
        if (!activity.isInActiveState)
            return

        val fragmentManager: FragmentManager = fragment.childFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.childFragmentContainer, UssdPaymentDetailsFragment())
        fragmentTransaction.addToBackStack(UssdPaymentDetailsFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    override fun openCardPaymentFragment(transactionReference: String, useSavedCard: Boolean) {
        if (!activity.isInActiveState)
            return

        val bundle = Bundle()

        bundle.putString(Constants.ARG_TRANSACTION_REF, transactionReference)
        bundle.putBoolean(Constants.ARG_USE_SAVED_CARD, useSavedCard)

        val cardPaymentFragment = CardPaymentFragment()
        cardPaymentFragment.arguments = bundle

        val fragmentManager = activity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, cardPaymentFragment)
        fragmentTransaction.addToBackStack(CardPaymentFragment::class.java.simpleName)
        fragmentTransaction.setCustomAnimations(
            R.anim.com_monnify_sdk_slide_in_from_right, R.anim.com_monnify_sdk_slide_out_to_left,
            R.anim.com_monnify_sdk_slide_in_from_left, R.anim.com_monnify_sdk_slide_out_to_right
        )
        fragmentTransaction.commit()
    }

    override fun openCardPaymentDetailsFragment(fragment: Fragment, useSavedCard: Boolean) {
        if (!activity.isInActiveState)
            return

        val bundle = Bundle()

        bundle.putBoolean(Constants.ARG_USE_SAVED_CARD, useSavedCard)

        val cardPaymentDetailsFragment = CardPaymentDetailsFragment()
        cardPaymentDetailsFragment.arguments = bundle

        val fragmentManager: FragmentManager = fragment.childFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.childFragmentContainer, cardPaymentDetailsFragment)
        fragmentTransaction.addToBackStack(CardPaymentDetailsFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    override fun openCardOtpFragment(fragment: Fragment) {
        if (!activity.isInActiveState)
            return

        val fragmentManager: FragmentManager = fragment.childFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.childFragmentContainer, CardPaymentOtpFragment())
        fragmentTransaction.addToBackStack(CardPaymentOtpFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    override fun open3DsAuthFragment(fragment: Fragment) {
        if (!activity.isInActiveState)
            return

        val fragmentManager: FragmentManager = fragment.childFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(
            R.id.childFragmentContainer,
            CardPaymentSecure3DAuthenticationFragment()
        )
        fragmentTransaction.addToBackStack(CardPaymentSecure3DAuthenticationFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    override fun openTransferPaymentFragment(
        transactionReference: String
    ) {
        if (!activity.isInActiveState)
            return

        val bundle = Bundle()
        bundle.putString(Constants.ARG_TRANSACTION_REF, transactionReference)
        val transferPaymentFragment = TransferPaymentFragment()
        transferPaymentFragment.arguments = bundle

        val fragmentManager = activity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, transferPaymentFragment)
        fragmentTransaction.addToBackStack(TransferPaymentFragment::class.java.simpleName)
        fragmentTransaction.setCustomAnimations(
            R.anim.com_monnify_sdk_slide_in_from_right, R.anim.com_monnify_sdk_slide_out_to_left,
            R.anim.com_monnify_sdk_slide_in_from_left, R.anim.com_monnify_sdk_slide_out_to_right
        )
        fragmentTransaction.commit()
    }

    override fun openPhonePaymentFragment(transactionReference: String) {
        if (!activity.isInActiveState)
            return

        val bundle = Bundle()
        bundle.putString(Constants.ARG_TRANSACTION_REF, transactionReference)
        val phonePaymentFragment = PhonePaymentFragment()
        phonePaymentFragment.arguments = bundle

        val fragmentManager = activity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, phonePaymentFragment)
        fragmentTransaction.addToBackStack(PhonePaymentFragment::class.java.simpleName)
        fragmentTransaction.setCustomAnimations(
            R.anim.com_monnify_sdk_slide_in_from_right, R.anim.com_monnify_sdk_slide_out_to_left,
            R.anim.com_monnify_sdk_slide_in_from_left, R.anim.com_monnify_sdk_slide_out_to_right
        )
        fragmentTransaction.commit()
    }

    override fun openPhonePaymentDetailsFragment(fragment: Fragment) {
        if (!activity.isInActiveState)
            return

        val fragmentManager: FragmentManager = fragment.childFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.childFragmentContainer, PhonePaymentDetailsFragment())
        fragmentTransaction.addToBackStack(PhonePaymentDetailsFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    override fun openPhonePaymentAuthorizationFragment(fragment: Fragment) {
        if (!activity.isInActiveState)
            return

        val fragmentManager: FragmentManager = fragment.childFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(
            R.id.childFragmentContainer,
            PhonePaymentAuthorizationFragment()
        )
        fragmentTransaction.addToBackStack(PhonePaymentAuthorizationFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    override fun openBankPaymentFragment(transactionReference: String) {
        if (!activity.isInActiveState)
            return

        val bundle = Bundle()
        bundle.putString(Constants.ARG_TRANSACTION_REF, transactionReference)
        val bankPaymentFragment = BankPaymentFragment()
        bankPaymentFragment.arguments = bundle

        val fragmentManager = activity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, bankPaymentFragment)
        fragmentTransaction.addToBackStack(BankPaymentFragment::class.java.simpleName)
        fragmentTransaction.setCustomAnimations(
            R.anim.com_monnify_sdk_slide_in_from_right, R.anim.com_monnify_sdk_slide_out_to_left,
            R.anim.com_monnify_sdk_slide_in_from_left, R.anim.com_monnify_sdk_slide_out_to_right
        )
        fragmentTransaction.commit()
    }

    override fun openBankPaymentSelectBankFragment(fragment: Fragment) {
        if (!activity.isInActiveState)
            return

        val fragmentManager: FragmentManager = fragment.childFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.childFragmentContainer, BankPaymentSelectBankFragment())
        fragmentTransaction.addToBackStack(BankPaymentSelectBankFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    override fun openBankPaymentDetailsFragment(fragment: Fragment) {
        if (!activity.isInActiveState)
            return

        val fragmentManager: FragmentManager = fragment.childFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.childFragmentContainer, BankPaymentDetailsFragment())
        fragmentTransaction.addToBackStack(BankPaymentDetailsFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    override fun openBankPaymentOtpFragment(fragment: Fragment) {
        if (!activity.isInActiveState)
            return

        val fragmentManager: FragmentManager = fragment.childFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.childFragmentContainer, BankPaymentOtpFragment())
        fragmentTransaction.addToBackStack(BankPaymentOtpFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    override fun openTransactionStatusFragment(transactionStatusDetails: TransactionStatusDetails) {
        val bundle = Bundle()
        bundle.putParcelable(Constants.ARG_TRANSACTION_STATUS_DETAILS, transactionStatusDetails)
        val transactionStatusFragment = TransactionStatusFragment()
        transactionStatusFragment.arguments = bundle

        val fragmentManager = activity.supportFragmentManager

        Logger.log(this, "activity.isInActiveState ${activity.isInActiveState}")

        if (activity.isInActiveState) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, transactionStatusFragment)
            fragmentTransaction.addToBackStack(TransactionStatusFragment::class.java.simpleName)
            fragmentTransaction.commit()
        } else {
            val deferredFragmentTransaction = object : DeferredFragmentTransaction() {
                override fun commit() {
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.fragmentContainer, transactionStatusFragment)
                    fragmentTransaction.addToBackStack(TransactionStatusFragment::class.java.simpleName)
                    fragmentTransaction.commit()
                }
            }

            deferredFragmentTransaction.contentFrameId = R.id.fragmentContainer
            deferredFragmentTransaction.replacingFragment = transactionStatusFragment

            activity.deferredFragmentTransactions.add(deferredFragmentTransaction)
        }
    }

    override fun goBackOnce() {
        activity.onBackPressed()
    }

}
