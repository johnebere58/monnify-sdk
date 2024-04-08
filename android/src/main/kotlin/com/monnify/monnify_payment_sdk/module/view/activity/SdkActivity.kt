package com.monnify.monnify_payment_sdk.module.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.monnify.monnify_payment_sdk.Monnify
import com.monnify.monnify_payment_sdk.R
import com.monnify.monnify_payment_sdk.Status
import com.monnify.monnify_payment_sdk.customview.MonnifyLoadingIndicatorView
import com.monnify.monnify_payment_sdk.customview.MonnifyMerchantHeader
import com.monnify.monnify_payment_sdk.customview.MonnifyTestIndicatorView
import com.monnify.monnify_payment_sdk.data.model.PaymentMethod
import com.monnify.monnify_payment_sdk.data.model.TransactionDetails
import com.monnify.monnify_payment_sdk.data.model.TransactionStatusDetails
import com.monnify.monnify_payment_sdk.module.AppNavigator
import com.monnify.monnify_payment_sdk.module.CommonUIFunctions
import com.monnify.monnify_payment_sdk.module.LocalMerchantKeyProvider
import com.monnify.monnify_payment_sdk.module.view.fragment.BaseFragment
import com.monnify.monnify_payment_sdk.module.view.fragment.MainFragment
import com.monnify.monnify_payment_sdk.module.vm.PaymentMethodsViewModel
import com.monnify.monnify_payment_sdk.service.ApplicationMode
import com.monnify.monnify_payment_sdk.service.RestService
import com.monnify.monnify_payment_sdk.service.StompService
import com.monnify.monnify_payment_sdk.util.Constants
import com.monnify.monnify_payment_sdk.util.DeferredFragmentTransaction
import com.monnify.monnify_payment_sdk.util.MoneyFormatter
import com.monnify.monnify_payment_sdk.util.SdkUtils
import io.reactivex.internal.functions.Functions.emptyConsumer
import io.reactivex.plugins.RxJavaPlugins
import java.math.BigDecimal
import java.util.*


class SdkActivity : AppCompatActivity(), LocalMerchantKeyProvider,
    CommonUIFunctions, BaseFragment.OnFragmentInteractionListener {

    private lateinit var monnifyHeader: MonnifyMerchantHeader
    private lateinit var monnifyLoadingIndicatorView: MonnifyLoadingIndicatorView
    private lateinit var monnifyTestIndicatorView: MonnifyTestIndicatorView

    private lateinit var paymentMethodsViewModel: PaymentMethodsViewModel
    private lateinit var transactionDetails: TransactionDetails

    var navigator: AppNavigator = AppNavigator(this)
    private val sdkUtils: SdkUtils = SdkUtils(this)

    var deferredFragmentTransactions: Queue<DeferredFragmentTransaction> = ArrayDeque()
    var isInActiveState: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.plugin_activity)
        setupActivity()
    }

    private fun setupActivity() {
        addGlobalErrorHandlerForRxJava()
        initViews()
        subscribeToViewModel()
        setupChildFragmentPopListener()
    }

    private fun initViews() {

        monnifyHeader = findViewById(R.id.monnifyHeaderView)
        monnifyLoadingIndicatorView = findViewById(R.id.monnifyLoadingIndicatorView)
        monnifyTestIndicatorView = findViewById(R.id.monnifyTestIndicator)

        if (Monnify.instance.getApplicationMode() == ApplicationMode.TEST) {
            monnifyTestIndicatorView.show()
        }

        transactionDetails =
            intent.getParcelableExtra(Constants.TRANSACTION_DETAILS_ARGS)!!


        monnifyHeader.setEmailAddress(transactionDetails.customerEmail)

        monnifyHeader.setOnCancelClickListener {
            this@SdkActivity.finish()
        }
    }

    private fun subscribeToViewModel() {
        paymentMethodsViewModel =
            ViewModelProvider(this)[PaymentMethodsViewModel::class.java]

        val bundle = Bundle()
        bundle.putParcelable(Constants.ARG_TRANSACTION_DETAILS, transactionDetails)

        paymentMethodsViewModel.init(
            bundle,
            this,
            navigator,
            this
        )

        paymentMethodsViewModel.liveEventInitializeTransaction.observe(
            this
        ) {
            navigator.openMainFragment()
        }

        paymentMethodsViewModel.liveSkipToPaymentMethod.observe(this) {

            val transactionReference = paymentMethodsViewModel.transactionReference
            when (it) {
                PaymentMethod.CARD -> navigator.openCardPaymentFragment(transactionReference)
                PaymentMethod.ACCOUNT_TRANSFER -> navigator.openTransferPaymentFragment(transactionReference)
                PaymentMethod.DIRECT_DEBIT -> navigator.openBankPaymentFragment(transactionReference)
                PaymentMethod.USSD -> navigator.openUssdPaymentFragment(transactionReference)
                PaymentMethod.PHONE_NUMBER -> navigator.openPhonePaymentFragment(transactionReference)
                else -> {}
            }
        }

        paymentMethodsViewModel.liveAmountBeingPaid.observe(this) {
            setTotalPayable(it!!)

            setAmount(BigDecimal(0))
            setPaymentFee(BigDecimal(0))
        }

        paymentMethodsViewModel.liveInitializeTransactionResponse.observe(this) {
            monnifyHeader.setMerchantName(it.merchantName)
            monnifyHeader.loadMerchantLogo(it.merchantLogoUrl)
        }

        paymentMethodsViewModel.liveError.observe(this) { event ->
            event?.getContentIfNotHandled()?.let {
                val transactionStatusDetails =
                    TransactionStatusDetails.Builder().status(Status.CANCELLED)
                        .message(it).build()

                navigator.openTransactionStatusFragment(transactionStatusDetails)
            }
        }
    }

    override fun getApiKey(): String {
        return Monnify.instance.getApiKey()
    }

    override fun getContractCode(): String {
        return Monnify.instance.getContractCode()
    }

    override fun getApplicationMode(): ApplicationMode {
        return Monnify.instance.getApplicationMode()
    }

    fun setActivityResult() {
        val intent = Intent()
        intent.putExtra(Monnify.instance.KEY_RESULT, Monnify.instance.paymentStatus)
        setResult(Activity.RESULT_OK, intent)
    }

    fun setActivityCancelled() {
        val intent = Intent()
        intent.putExtra(Monnify.instance.KEY_RESULT, Monnify.instance.paymentStatus)
        setResult(Activity.RESULT_CANCELED, intent)
    }

    private fun setupChildFragmentPopListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            val index = this.supportFragmentManager.backStackEntryCount - 1
            val backEntry = supportFragmentManager.getBackStackEntryAt(index)
            if (backEntry.name == MainFragment::class.java.simpleName)
                setTotalPayable(paymentMethodsViewModel.initializeTransactionRequest.amount)
            setAmount(paymentMethodsViewModel.initializeTransactionRequest.amount)
            setPaymentFee(BigDecimal.ZERO)
        }
    }

    fun setTotalPayable(amount: BigDecimal) {
        val currencyCode = paymentMethodsViewModel.initializeTransactionRequest.currencyCode

        val formattedAmount = MoneyFormatter.format(amount, currencyCode)
        monnifyHeader.setTotalAmount(formattedAmount)
    }

    fun setAmount(amount: BigDecimal) {
        val currencyCode = paymentMethodsViewModel.initializeTransactionRequest.currencyCode

        val formattedAmount = MoneyFormatter.format(amount, currencyCode)
        monnifyHeader.setAmount(formattedAmount)
    }

    fun setPaymentFee(amount: BigDecimal) {
        val currencyCode = paymentMethodsViewModel.initializeTransactionRequest.currencyCode

        val formattedAmount = MoneyFormatter.format(amount, currencyCode)
        monnifyHeader.setFee(formattedAmount)
    }

    override fun onBackPressed() {
        super.onBackPressed();
        if (supportFragmentManager.backStackEntryCount > 1) {

            supportFragmentManager.popBackStack()
        } else
            finish()
    }

    /*
    * Common UI Implementations
    * */

    override fun showToastMessage(message: String) {
        sdkUtils.showToastMessage(message)
    }

    override fun showLoading(messageResId: Int) {
        hideKeyboard()

        monnifyLoadingIndicatorView.visibility = VISIBLE
        monnifyLoadingIndicatorView.showLoadingIndicator(getString(messageResId))
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    override fun dismissLoading() {
        monnifyLoadingIndicatorView.hideLoadingIndicator()
        monnifyLoadingIndicatorView.visibility = GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

    }

    private fun hideKeyboard() {
        if (window != null) {
            window.decorView
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        RestService.destroySingleton()
        StompService.destroySingleton()
    }

    override fun onResume() {
        super.onResume()
        isInActiveState = true
    }

    override fun onPause() {
        super.onPause()
        isInActiveState = false
    }

    override fun onPostResume() {
        super.onPostResume()
        while (!deferredFragmentTransactions.isEmpty()) {
            deferredFragmentTransactions.remove().commit()
        }
    }

    override fun onFragmentInteraction(uri: Uri) {
        //
    }

    companion object {
        init {
            // This enables pre-Lollipop devices to display vector drawables
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    // This should be in the App class, but we don't have an app class. :)
    private fun addGlobalErrorHandlerForRxJava() {
        RxJavaPlugins.setErrorHandler(emptyConsumer())
    }
}
