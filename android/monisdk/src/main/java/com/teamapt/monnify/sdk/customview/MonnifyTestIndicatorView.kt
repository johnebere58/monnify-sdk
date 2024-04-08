package com.teamapt.monnify.sdk.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import com.teamapt.monnify.sdk.R

class MonnifyTestIndicatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayoutCompat(context, attrs) {


    init {
        inflate(context, R.layout.plugin_monnify_test_indicator, this)
    }


    fun show() {
        visibility = VISIBLE
    }

    fun hide() {
        visibility = GONE
    }
}