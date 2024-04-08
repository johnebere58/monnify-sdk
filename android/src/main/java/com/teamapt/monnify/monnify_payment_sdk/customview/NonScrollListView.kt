package com.teamapt.monnify.monnify_payment_sdk.customview

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ListView

class NonScrollListView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ListView(context, attrs) {


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val customHeightMeasureSpec: Int =
            MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr  2, MeasureSpec.AT_MOST)

        super.onMeasure(widthMeasureSpec, customHeightMeasureSpec)


        val params: ViewGroup.LayoutParams = layoutParams
        params.height = measuredHeight
    }

}