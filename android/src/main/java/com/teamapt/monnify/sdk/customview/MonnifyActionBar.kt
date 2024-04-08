package com.teamapt.monnify.sdk.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.monnify.monnify_payment_sdk.R

class MonnifyActionBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var customerNameTextView: AppCompatTextView
    private var cancelButtonTextView: AppCompatTextView

    init {
        inflate(context, R.layout.plugin_action_bar, this)

        customerNameTextView = findViewById(R.id.customerNameTextView)
        cancelButtonTextView = findViewById(R.id.cancelButtonTextView)
    }

    fun setOnCancelButtonClick(listen: () -> Unit) {
        cancelButtonTextView.setOnClickListener {
            listen.invoke()
        }
    }
}