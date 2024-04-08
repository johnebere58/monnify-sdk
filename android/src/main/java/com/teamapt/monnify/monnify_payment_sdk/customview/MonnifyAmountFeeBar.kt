package com.teamapt.monnify.monnify_payment_sdk.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.monnify.monnify_payment_sdk.R

class MonnifyAmountFeeBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var amountTextView : AppCompatTextView
    private var feeTextView : AppCompatTextView

    init {
        inflate(context, R.layout.plugin_amount_fee_bar, this)
        amountTextView = findViewById(R.id.feeBarAmountTextView)
        feeTextView = findViewById(R.id.feeBarFeeTextView)
    }
}