package com.teamapt.monnify.monnify_payment_sdk.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.monnify.monnify_payment_sdk.R

class MonnifyLoadingIndicatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {


    var text: CharSequence
        get() = progressTitleTextView.text
        set(value) {
            progressTitleTextView.text = value
        }


    private val progressTitleTextView: AppCompatTextView
    private val progressIndicator: CircularProgressIndicator

    init {
        inflate(context, R.layout.plugin_monnify_loading_indicator, this)

        progressTitleTextView = findViewById(R.id.progressTitleTextView)
        progressIndicator = findViewById(R.id.circularProgressIndicator)
    }

    fun showLoadingIndicator(message: String?) {
        progressTitleTextView.text = message ?: ""
    }

    fun hideLoadingIndicator() {
        progressTitleTextView.text = ""
    }
}