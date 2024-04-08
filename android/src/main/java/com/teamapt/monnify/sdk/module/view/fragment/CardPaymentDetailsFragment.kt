package com.teamapt.monnify.sdk.module.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.ViewModelProvider
import com.teamapt.monnify.sdk.Monnify
import com.monnify.monnify_payment_sdk.R
import com.teamapt.monnify.sdk.customview.*
import com.teamapt.monnify.sdk.module.vm.CardPaymentViewModel
import com.teamapt.monnify.sdk.module.vm.PaymentMethodsViewModel
import com.teamapt.monnify.sdk.service.ApplicationMode
import com.teamapt.monnify.sdk.util.Constants
import com.teamapt.monnify.sdk.util.Logger
import com.teamapt.monnify.sdk.util.TestCard
import com.teamapt.monnify.sdk.util.toEditable

class CardPaymentDetailsFragment : BaseFragment() {

    private lateinit var cardPaymentViewModel: CardPaymentViewModel
    private lateinit var paymentMethodViewModel: PaymentMethodsViewModel

    private lateinit var paymentMethodsViewModel: PaymentMethodsViewModel

    private lateinit var cardNumberEditText: MonnifyCardNumberEditText
    private lateinit var expiryDateAndCVVInputView: MonnifyExpiryDateAndCVVInputView
    private lateinit var continueButton: MonnifyRoundedOrangeGradientButton
    private lateinit var pinEntryLayout: LinearLayoutCompat
    private lateinit var cardPinView: PinView

    private lateinit var testCardLayout: LinearLayoutCompat
    private var testCardsDropDownView: MonnifyDropDownView<TestCard>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.plugin_fragment_card_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardNumberEditText = view.findViewById(R.id.cardNumberEditText)
        expiryDateAndCVVInputView = view.findViewById(R.id.expiryDateAndCVVInputView)
        continueButton = view.findViewById(R.id.continueButton)
        pinEntryLayout = view.findViewById(R.id.pinEntryLayout)
        cardPinView = view.findViewById(R.id.pinView)

        testCardLayout = view.findViewById(R.id.testCardsLayout)
        testCardsDropDownView = view.findViewById(R.id.testCardsDropDownView)

        setPropertiesToViews()
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        paymentMethodViewModel =
            ViewModelProvider(requireActivity()).get(PaymentMethodsViewModel::class.java)

        paymentMethodsViewModel =
            ViewModelProvider(requireActivity()).get(PaymentMethodsViewModel::class.java)

        cardPaymentViewModel =
            ViewModelProvider(requireParentFragment()).get(CardPaymentViewModel::class.java)

        cardPaymentViewModel.liveOpenOtpFragment.observe(this) {
            if (it) {
                getNavigator().openCardOtpFragment(requireParentFragment())
            }
        }

        cardPaymentViewModel.liveOpenSecure3DAuthFragment.observe(this) {
            if (it) {
                getNavigator().open3DsAuthFragment(requireParentFragment())
            }
        }

        cardPaymentViewModel.liveCardNumberValidation.observe(this) {
            if (it!!) {
                cardPaymentViewModel.checkCardRequirements()
            }
            cardNumberEditText.setError(if (it) null else getString(R.string.invalid_card_number))
        }

        cardPaymentViewModel.liveCardType.observe(this) {
            cardNumberEditText.cardType = it!!
        }

        cardPaymentViewModel.liveCardExpiryDateValidation.observe(this) {
            expiryDateAndCVVInputView.setExpiryDateError(if (it!!) null else getString(R.string.invalid_expiry_date))
        }

        cardPaymentViewModel.liveCardCvvValidation.observe(this) {
            expiryDateAndCVVInputView.setCVVError(if (it!!) null else getString(R.string.invalid_cvv))
        }

        cardPaymentViewModel.liveCardValidation.observe(this) {
            continueButton.state =
                if (it!!) MonnifyRoundedOrangeGradientButton.ButtonState.ENABLED else MonnifyRoundedOrangeGradientButton.ButtonState.DISABLED
        }

        cardPaymentViewModel.liveError.observe(this) { event ->
            event?.getContentIfNotHandled()?.let {
                errorTextView?.setTextAndMakeVisibleWithTimeout(it)
            }
        }

        cardPaymentViewModel.liveCardPinRequired.observe(this) {
            if (it!!) {
                showPinView()
            } else {
                hidePinView()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setPropertiesToViews() {
        addTextWatchersToEditViews()
        fillCardDetailsIfAllowed()

        cardPinView.setAnimationEnable(true)
        continueButton.setOnClickListener {
            paymentMethodViewModel.saveCard(cardPaymentViewModel.currentCard)
            cardPaymentViewModel.verifyCardAndInitializePayOnSuccess() }

        if (Monnify.instance.getApplicationMode() == ApplicationMode.TEST) {

            testCardLayout.visibility = View.VISIBLE
            val testCards = TestCard.testCards()
            val hint = TestCard.testCardDropdownHint()

            testCardsDropDownView?.setupView(getString(R.string.select_a_test_card),
                testCards,
                hint,
                object : MonnifyDropDownView.OnDropdownItemSelectedListener<TestCard> {
                    override fun onDropdownItemSelected(selectedItem: TestCard?) {
                        selectedItem?.let {
                            fillTestCardDetails(it)
                        }
                    }
                })
        }

        expiryDateAndCVVInputView.setupCVVToolTip()
    }

    private fun hidePinView() {
        pinEntryLayout.visibility = View.GONE
    }

    private fun showPinView() {
        pinEntryLayout.visibility = View.VISIBLE
    }

    private fun fillCardDetailsIfAllowed() {
        if (requireArguments().getBoolean(Constants.ARG_USE_SAVED_CARD, false)) {
            val card = paymentMethodsViewModel.savedCardForPayWithSameCard

            if (card != null) {
                val expiryDate =
                    "${if (card.expiryMonth!! < 10) "0" else ""}${card.expiryMonth}/${
                        card.expiryYear?.rem(
                            100
                        )
                    }"

                cardNumberEditText.cardNumber = card.number!!
                expiryDateAndCVVInputView.cvv = card.cvv!!
                expiryDateAndCVVInputView.expiryDate = expiryDate
            }
        }
    }

    private fun fillTestCardDetails(card: TestCard) {
        cardNumberEditText.cardNumber = card.number!!
        expiryDateAndCVVInputView.cvv = card.cvv!!
        expiryDateAndCVVInputView.expiryDate = card.expiryDate!!
        cardPinView.text = (if (card.pin != null) card.pin!! else "").toEditable()
    }

    private fun addTextWatchersToEditViews() {

        cardNumberEditText.eventListener =
            object : MonnifyCardNumberEditText.CardNumberInputEventListener {
                override fun onViewLoseFocus() {
                    Logger.log(this, "cardNumberEditText onViewLoseFocus called")
                }

                override fun onCardTextChanged(cardNumber: String) {
                    cardPaymentViewModel.setCardNumberAndVerify(cardNumber)
                }
            }

        expiryDateAndCVVInputView.eventListener =
            object : MonnifyExpiryDateAndCVVInputView.ExpiryDateAndCVVInputEventListener {
                override fun onCVVChanged(cvv: String) {
                    cardPaymentViewModel.setCardCvvAndVerify(cvv)
                }

                override fun onExpiryDateChanged(expiryDate: String) {
                    cardPaymentViewModel.setCardExpiryAndVerify(expiryDate)
                }
            }

        cardPinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                cardPaymentViewModel.setCardPinAndVerify(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                //
            }
        })
    }

    private fun getParentRootView(): ViewGroup {
        return (requireParentFragment() as CardPaymentFragment).rootView
    }
}