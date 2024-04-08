package com.monnify.monnify_payment_sdk.module.view.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teamapt.monnify.pos.util.ViewModelConstructor
import com.teamapt.monnify.pos.util.ViewModelFactory
import com.monnify.monnify_payment_sdk.R
import com.monnify.monnify_payment_sdk.customview.MonnifyLetterSpacingTextView
import com.monnify.monnify_payment_sdk.customview.MonnifyRoundedOrangeGradientButton
import com.monnify.monnify_payment_sdk.module.vm.UssdPaymentViewModel
import com.monnify.monnify_payment_sdk.util.BanksProvider
import com.monnify.monnify_payment_sdk.util.Logger
import java.math.BigDecimal

class UssdPaymentDetailsFragment : BaseFragment() {

    private lateinit var banksProvider: BanksProvider

    private lateinit var ussdPaymentViewModel: UssdPaymentViewModel

    private lateinit var monnifyUssdBankName: MonnifyLetterSpacingTextView
    private lateinit var tapToCopyLayout: LinearLayoutCompat
    private lateinit var shortCodeTextView: AppCompatTextView
    private lateinit var clickToCopyCode: AppCompatTextView
    private lateinit var selectAnotherBankButton: AppCompatButton
    private lateinit var continueButton: MonnifyRoundedOrangeGradientButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        banksProvider = BanksProvider(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.plugin_fragment_ussd_payment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView(view)
        setupView()
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


        ussdPaymentViewModel.liveUssdPaymentResponse.observe(this) {

            getActivityAsSdkActivity().setTotalPayable(it?.authorizedAmount ?: BigDecimal.ZERO)
            getActivityAsSdkActivity().setAmount(it?.authorizedAmount ?: BigDecimal.ZERO)
            getActivityAsSdkActivity().setPaymentFee(BigDecimal.ZERO)

            shortCodeTextView.text = it.paymentCode
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

        ussdPaymentViewModel.liveVerifyingTransaction.observe(this) {
            continueButton.state =
                if (it) MonnifyRoundedOrangeGradientButton.ButtonState.LOADING
                else MonnifyRoundedOrangeGradientButton.ButtonState.ENABLED

            continueButton.text =
                if (it) getString(R.string.checking_for_payment)
                else getString(R.string.i_have_completed_this_payment)

        }
    }

    private fun initView(view: View) {

        monnifyUssdBankName = view.findViewById(R.id.monnifyUssdBankName)
        tapToCopyLayout = view.findViewById(R.id.tapToCopyLayout)
        shortCodeTextView = view.findViewById(R.id.shortCodeTextView)
        clickToCopyCode = view.findViewById(R.id.clickToCopyCodeTextView)
        selectAnotherBankButton = view.findViewById(R.id.selectAnotherBankButton)
        continueButton = view.findViewById(R.id.continueButton)

        errorTextView = view.findViewById(R.id.monnifyErrorTextView)
    }

    private fun setupView() {
        monnifyUssdBankName.text = ussdPaymentViewModel.selectedBank?.name ?: ""

        selectAnotherBankButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        clickToCopyCode.setOnClickListener {
            copyDisplayedUSSDCode()
        }

        tapToCopyLayout.setOnClickListener {
            dialDisplayedUSSDCode()
        }

        continueButton.setOnClickListener {
            ussdPaymentViewModel.verifyTransaction(shouldComplete = false)
        }
    }

    private fun dialDisplayedUSSDCode() {
        try {
            val ussdCode = shortCodeTextView.text

            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + Uri.encode(ussdCode.toString()))
            startActivity(intent)
        } catch (e: Exception) {
            Logger.log(this, e.message, Logger.LEVEL.ERROR)
        }
    }

    private fun copyDisplayedUSSDCode() {
        val ussdCode = shortCodeTextView.text

        val clipboard =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("ussdcode", ussdCode)
        clipboard?.setPrimaryClip(clip)

        clickToCopyCode.text = getString(R.string.copied)

        Handler(Looper.getMainLooper()).postDelayed({
            clickToCopyCode.text = getString(R.string.click_to_copy_code)
        }, 1000)
    }


    override fun onDestroy() {
        super.onDestroy()
        ussdPaymentViewModel.stopListening()
    }
}