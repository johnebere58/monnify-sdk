package com.teamapt.monnify.sdk.customview

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.teamapt.monnify.sdk.R
import com.teamapt.monnify.sdk.data.model.CardType
import com.teamapt.monnify.sdk.util.SdkUtils

class MonnifyCardNumberEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayoutCompat(context, attrs) {


    var cardNumber: CharSequence
        get() = inputEditText.text.toString()
        set(value) = inputEditText.setText(value)

    var cardType: CardType = CardType.UNKNOWN
        set(value) {

            when (value) {
                CardType.VISA ->
                    rightIconImageView.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_visa)
                    )
                CardType.MASTERCARD ->
                    rightIconImageView.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_mastercard_logo)
                    )
                CardType.VERVE ->
                    rightIconImageView.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_verve)
                    )
                CardType.AMERICAN_EXPRESS ->
                    rightIconImageView.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_amex)
                    )
                CardType.DISCOVER ->
                    rightIconImageView.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_discover)
                    )
                CardType.DINERS_CLUB ->
                    rightIconImageView.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_diners)
                    )
                CardType.JCB ->
                    rightIconImageView.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_jcb)
                    )
                else -> {
                    rightIconImageView.setImageResource(android.R.color.transparent)
                }
            }
            field = value
        }

    private val textInputLayer: TextInputLayout
    private val inputEditText: TextInputEditText
    private val rightIconImageView: AppCompatImageView
    private val statusMessageTextView: AppCompatTextView

    init {
        inflate(context, R.layout.plugin_card_number_edit_text, this)

        textInputLayer = findViewById(R.id.textInputLayout)
        inputEditText = findViewById(R.id.inputEditText)
        rightIconImageView = findViewById(R.id.rightIconImageView)
        statusMessageTextView = findViewById(R.id.statusMessageTextView)

        setupView()
    }

    interface CardNumberInputEventListener {
        fun onCardTextChanged(cardNumber: String)
        fun onViewLoseFocus()
    }

    var eventListener: CardNumberInputEventListener? = null
        set(value) {
            if (value != null) {
                inputEditText.addTextChangedListener(CreditCardNumberFormattingTextWatcher(value))
            }
        }

    private fun setupView() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            inputEditText.letterSpacing = 0.1f

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                inputEditText.hint = context.getString(R.string.card_number_placeholder)
                statusMessageTextView.visibility = GONE
            } else {
                inputEditText.hint = ""
                eventListener?.onViewLoseFocus()
            }
        }
    }

    fun setError(errorMessage: String?) {
        statusMessageTextView.visibility = VISIBLE
        statusMessageTextView.text = errorMessage
    }


    private fun removeWhiteSpacesFrom(text: String): String {

        val builder = StringBuilder()
        for (c in text) {
            if (SdkUtils.isANumber(c)) {
                builder.append(c)
            }
        }

        return builder.toString()
    }


    inner class CreditCardNumberFormattingTextWatcher(private val eventListener: CardNumberInputEventListener) :
        TextWatcher {

        private val emptyString = ""
        private val whiteSpace = " "
        private var lastSource = emptyString

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            //
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //
        }

        override fun afterTextChanged(s: Editable) {
            var text = s.toString()
            if (lastSource != text) {
                text = text.replace(whiteSpace, emptyString)
                val stringBuilder = java.lang.StringBuilder()
                for (i in text.indices) {
                    if (i > 0 && i % 4 == 0) {
                        stringBuilder.append(whiteSpace)
                    }
                    stringBuilder.append(text[i])
                }
                lastSource = stringBuilder.toString()
                s.replace(0, s.length, lastSource)
            }

            eventListener.onCardTextChanged(removeWhiteSpacesFrom(text))
        }
    }
}