package com.teamapt.monnify.sdk.customview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView

class TouchableWebView : WebView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun onTouchEvent(event: MotionEvent): Boolean {

        // Prevent crash if activity is hardware accelerated
        if (event.findPointerIndex(0) == -1) {
            return super.onTouchEvent(event)
        }

        requestDisallowInterceptTouchEvent(true)

        performClick()
        return super.onTouchEvent(event)
    }

}