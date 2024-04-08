package com.teamapt.monnify.sdk.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginStart
import com.monnify.monnify_payment_sdk.R

class MonnifyPaymentMethodHeaderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayoutCompat(context, attrs) {

    private val paymentMethodLayout: LinearLayoutCompat
    private val paymentMethodIconView: AppCompatImageView
    private val paymentMethodTextView: AppCompatTextView
    private val changePaymentMethodTextView: AppCompatTextView

    private val background: Int
    private val buttonText: String
    private val paymentMethodIcon: Int
    private val paymentMethodTitle: CharSequence
    private val horizontalMargin: Int

    init {
        inflate(context, R.layout.plugin_payment_method_header, this)

        paymentMethodLayout = findViewById(R.id.paymentMethodHeaderLayout)
        paymentMethodIconView = findViewById(R.id.paymentMethodHeaderIconImageView)
        paymentMethodTextView = findViewById(R.id.paymentMethodHeaderTitleTextView)

        changePaymentMethodTextView = findViewById(R.id.paymentMethodHeaderChangeMethodTextView)


        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.MonnifyPaymentMethodHeaderView)

        typedArray.apply {
            try {
                background = getResourceId(
                    R.styleable.MonnifyPaymentMethodHeaderView_android_background,
                    R.drawable.bg_payment_method_header
                )

                buttonText =
                    getString(R.styleable.MonnifyPaymentMethodHeaderView_paymentMethodButtonText)
                        ?: context.resources.getString(R.string.change)

                paymentMethodIcon = getResourceId(
                    R.styleable.MonnifyPaymentMethodHeaderView_paymentMethodIcon,
                    android.R.color.transparent
                )

                paymentMethodTitle =
                    getString(R.styleable.MonnifyPaymentMethodHeaderView_paymentMethodTitle) ?: ""

                horizontalMargin = getDimensionPixelSize(
                    R.styleable.MonnifyPaymentMethodHeaderView_paymentMethodHorizontalMargin,
                    context.resources.getDimensionPixelSize(R.dimen.dimen24dp)
                )

                text = paymentMethodTitle
                icon = paymentMethodIcon

                paymentMethodLayout.setBackgroundResource(background)
                changePaymentMethodTextView.text = buttonText
            } finally {
                recycle()
            }
        }
    }

    var text: CharSequence = ""
        set(value) {
            field = value
            paymentMethodTextView.text = field
        }

    @DrawableRes
    var icon: Int = 0
        set(value) {
            field = value
            if (field != 0)
                paymentMethodIconView.setImageResource(field)
        }


    fun setOnChangeButtonClickListener(listen: () -> Unit) {
        changePaymentMethodTextView.setOnClickListener { listen.invoke() }
    }

    fun setOnChangeButtonVisibility(isVisible: Boolean) {
        changePaymentMethodTextView.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }
}