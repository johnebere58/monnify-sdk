package com.teamapt.monnify.monnify_payment_sdk.customview

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import kotlin.math.max

class MonnifyLinearLayoutCompat @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayoutCompat(context, attrs) {


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val count = childCount

        var maxHeight = 0
        var maxWidth = 0

        measureChildren(widthMeasureSpec, heightMeasureSpec)

        for (i in 0 until count) {
            val child: View = getChildAt(i)
            if (child.visibility != View.GONE) {
                val childHeight = child.measuredHeight
                val childWidth = child.measuredWidth

                maxWidth = max(maxWidth, childWidth)
                maxHeight = max(maxHeight, childHeight)
            }

            maxWidth += if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                paddingStart + paddingEnd
            } else {
                paddingLeft + paddingRight
            }
            maxHeight += paddingTop + paddingBottom

            maxHeight = max(maxHeight, suggestedMinimumHeight)
            maxWidth = max(maxWidth, suggestedMinimumWidth)

            setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));

        }
    }
}