package com.monnify.monnify_payment_sdk.module.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.teamapt.monnify.pos.util.ViewModelConstructor
import com.teamapt.monnify.pos.util.ViewModelFactory
import com.monnify.monnify_payment_sdk.R
import com.monnify.monnify_payment_sdk.customview.MonnifyDropDownView
import com.monnify.monnify_payment_sdk.customview.MonnifyEditText
import com.monnify.monnify_payment_sdk.customview.MonnifyRoundedOrangeGradientButton
import com.monnify.monnify_payment_sdk.module.vm.BankPaymentViewModel
import com.monnify.monnify_payment_sdk.rest.data.response.Bank
import com.monnify.monnify_payment_sdk.util.BanksProvider
import java.text.SimpleDateFormat
import java.util.*

class BankPaymentDetailsFragment : BaseFragment() {

    private lateinit var bankPaymentViewModel: BankPaymentViewModel

    private lateinit var accountInformationLayout: LinearLayoutCompat
    private lateinit var banksDropDownView: MonnifyDropDownView<Bank>
    private lateinit var accountNumberEditText: MonnifyEditText
    private lateinit var additionalInformationLayout: LinearLayoutCompat
    private lateinit var bvnEditText: MonnifyEditText
    private lateinit var dateOfBirthEditText: MonnifyEditText
    private lateinit var accountDetailsLayout: LinearLayoutCompat
    private lateinit var accountNameTextView: AppCompatTextView
    private lateinit var continueButton: MonnifyRoundedOrangeGradientButton
    private lateinit var goBackButton: AppCompatButton


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.plugin_fragment_bank_payment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountInformationLayout = view.findViewById(R.id.accountInformationLayout)
        banksDropDownView = view.findViewById(R.id.banksDropDownView)
        accountNumberEditText = view.findViewById(R.id.accountNumberEditText)
        additionalInformationLayout = view.findViewById(R.id.additionalInformationLayout)
        bvnEditText = view.findViewById(R.id.bvnEditText)
        dateOfBirthEditText = view.findViewById(R.id.dateOfBirthEditText)
        accountDetailsLayout = view.findViewById(R.id.customerNameLayout)
        accountNameTextView = view.findViewById(R.id.customerNameTextView)
        continueButton = view.findViewById(R.id.continueButton)
        goBackButton = view.findViewById(R.id.goBackButton)


        errorTextView = view.findViewById(R.id.monnifyErrorTextView)

        setupView()
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
            hideAccountInfo()
        }

        bankPaymentViewModel.liveBankPaymentVerifyAccount.observe(this) {
            accountNameTextView.text = it?.accountName ?: ""
            showAccountInfo()

            if (it.additionalInfoRequired == true) {
                setupAdditionalInfo()
            } else {
                setupConfirmation()
            }
        }

        bankPaymentViewModel.liveBankPaymentChargeAccount.observe(this) {

            getNavigator().openBankPaymentOtpFragment(requireParentFragment())
        }

        bankPaymentViewModel.liveSetBankPaymentState.observe(this) {
            when (it) {
                BankPaymentViewModel.Companion.BankPaymentState.VALIDATE -> {
                    bankPaymentViewModel.activateButton(false)
                    continueButton.text = getString(R.string.validate_details)
                }
                BankPaymentViewModel.Companion.BankPaymentState.CONFIRM -> {
                    bankPaymentViewModel.activateButton(true)
                    continueButton.text = getString(R.string.confirm)
                }
                else -> {
                    bankPaymentViewModel.activateButton(false)
                    continueButton.text = getString(R.string.verify_account)
                }
            }
        }

        bankPaymentViewModel.liveEventActivateContinueButton.observe(this) {
            continueButton.state =
                if (it)
                    MonnifyRoundedOrangeGradientButton.ButtonState.ENABLED
                else
                    MonnifyRoundedOrangeGradientButton.ButtonState.DISABLED
        }

        bankPaymentViewModel.liveEventSetContinueButtonLoading.observe(this) {
            continueButton.state =
                if (it)
                    MonnifyRoundedOrangeGradientButton.ButtonState.LOADING
                else
                    MonnifyRoundedOrangeGradientButton.ButtonState.ENABLED

            if (it)
                updateButtonTextToMatchLoading()
            else
                updateButtonTextToMatchEnable()
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

    private fun updateButtonTextToMatchEnable() {
        when (bankPaymentViewModel.liveSetBankPaymentState.value) {
            BankPaymentViewModel.Companion.BankPaymentState.VALIDATE -> {
                continueButton.text = getString(R.string.validate_details)
            }
            BankPaymentViewModel.Companion.BankPaymentState.CONFIRM -> {
                continueButton.text = getString(R.string.proceed)
            }
            else -> {
                continueButton.text = getString(R.string.verify_account)
            }
        }
    }

    private fun updateButtonTextToMatchLoading() {
        when (bankPaymentViewModel.liveSetBankPaymentState.value) {
            BankPaymentViewModel.Companion.BankPaymentState.VALIDATE -> {
                continueButton.text = getString(R.string.validating_details)
            }
            BankPaymentViewModel.Companion.BankPaymentState.CONFIRM -> {
                continueButton.text = getString(R.string.proceeding)
            }
            else -> {
                continueButton.text = getString(R.string.verifying_account)
            }
        }
    }

    private fun setupAdditionalInfo() {
        additionalInformationLayout.visibility = View.VISIBLE
        accountInformationLayout.visibility = View.GONE

        bankPaymentViewModel.setBankPaymentState(BankPaymentViewModel.Companion.BankPaymentState.VALIDATE)
    }

    private fun setupConfirmation() {
        bankPaymentViewModel.setBankPaymentState(BankPaymentViewModel.Companion.BankPaymentState.CONFIRM)
    }

    private fun hideAdditionalInfo() {
        additionalInformationLayout.visibility = View.GONE
        accountInformationLayout.visibility = View.VISIBLE
    }

    private fun showAccountInfo() {
        accountDetailsLayout.visibility = View.VISIBLE
    }

    private fun hideAccountInfo() {
        accountDetailsLayout.visibility = View.GONE
    }

    private fun setupView() {

        goBackButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        continueButton.setOnClickListener {
            bankPaymentViewModel.processBankPaymentState()
        }

        dateOfBirthEditText.setOnClickListener {
            showDatePicker()
        }

        addEventListenerToEditText()
    }

    private fun showDatePicker() {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)

        val today = MaterialDatePicker.todayInUtcMilliseconds()

        val constraintBuilder = CalendarConstraints.Builder().setEnd(today)

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(constraintBuilder.build())
                .setTitleText(getString(R.string.select_date_of_birth))
                .build()

        datePicker.addOnPositiveButtonClickListener {
            val formattedDate = sdf.format(it)

            dateOfBirthEditText.text = formattedDate
        }
        datePicker.show(childFragmentManager, "datePicker")
    }

    private fun addEventListenerToEditText() {
        accountNumberEditText.eventListener =
            object : MonnifyEditText.MonnifyEditTextInputEventListener {
                override fun onViewLoseFocus() {
                    //
                }

                override fun onTextChanged(text: CharSequence) {
                    hideAccountInfo()
                    bankPaymentViewModel.setAccountNumber(text.toString())
                    bankPaymentViewModel.activateButton(accountNumberEditText.validate())
                }

                override fun onValidate(text: CharSequence?): Boolean {
                    return text?.length ?: 0 == 10
                }
            }

        bvnEditText.eventListener = object : MonnifyEditText.MonnifyEditTextInputEventListener {
            override fun onViewLoseFocus() {
                //
            }

            override fun onTextChanged(text: CharSequence) {
                bankPaymentViewModel.setBvn(text.toString())
                bankPaymentViewModel.activateButton(bvnEditText.validate() && dateOfBirthEditText.isNotEmpty())
            }

            override fun onValidate(text: CharSequence?): Boolean {
                return text?.length ?: 0 == 11
            }
        }

        dateOfBirthEditText.eventListener =
            object : MonnifyEditText.MonnifyEditTextInputEventListener {
                override fun onViewLoseFocus() {
                    //
                }

                override fun onTextChanged(text: CharSequence) {
                    bankPaymentViewModel.setDateOfBirth(text.toString())
                    bankPaymentViewModel.activateButton(dateOfBirthEditText.validate() && bvnEditText.isNotEmpty())
                }

                override fun onValidate(text: CharSequence?): Boolean {
                    return !(text.isNullOrBlank() || text.isNullOrEmpty())
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

        if (banksDropDownView.getSelectedItemPosition() == 0 && bankPaymentViewModel.getSelectedBank() != null) {
            banksDropDownView.setSelectedItem(bankPaymentViewModel.getSelectedBank()!!)
        }
    }

}