package com.teamapt.monnify.sdk.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.teamapt.monnify.sdk.R

class MonnifyBrandContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    init {
        inflate(context, R.layout.plugin_monnify_brand_text_view, this)
    }
}