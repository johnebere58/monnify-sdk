package com.monnify.monnify_payment_sdk.customview

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ScaleXSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.monnify.monnify_payment_sdk.R

class MonnifyLetterSpacingTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var ltrSpacing: Float = 0f

    private var originalText: CharSequence = ""

    init {

        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.MonnifyLetterSpacingTextView)

        typedArray.apply {

            try {
                originalText =
                    getString(R.styleable.MonnifyLetterSpacingTextView_android_text) ?: ""
                ltrSpacing =
                    getFloat(R.styleable.MonnifyLetterSpacingTextView_android_letterSpacing, 0f)
            } finally {
                recycle()
            }
        }

        applyLetterSpacing()
        this.invalidate()
    }

    private fun applyLetterSpacing() {

        val builder: StringBuilder = StringBuilder()

        for (x: Int in 1..originalText.length) {
            builder.append(originalText[x])
            if (x < originalText.length) {
                builder.append("\u00A0")
            }
        }

        val spannableString = SpannableString(builder.toString())

        if (builder.toString().length > 1) {
            for (x: Int in 1..builder.toString().length step 2) {
                spannableString.setSpan(
                    ScaleXSpan((ltrSpacing + 1 / 10f)),
                    x,
                    x + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        super.setText(spannableString, BufferType.SPANNABLE)
    }

}