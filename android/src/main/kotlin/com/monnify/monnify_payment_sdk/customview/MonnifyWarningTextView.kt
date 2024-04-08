package com.monnify.monnify_payment_sdk.customview

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.monnify.monnify_payment_sdk.R

class MonnifyWarningTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayoutCompat(context, attrs) {


    companion object {
        private const val DEFAULT_TIME_SECONDS_BEFORE_MESSAGE_IS_REMOVED = 3
        private const val MAX_TIME_SECONDS_BEFORE_MESSAGE_IS_REMOVED = 15
    }

    private val waringTextView: AppCompatTextView

    private var countDownTimer: CountDownTimer? = null

    val text: CharSequence
        get() = waringTextView.text

    init {
        inflate(context, R.layout.plugin_monnify_warning_text_view, this)

        waringTextView = findViewById(R.id.warningMessageTextView)
    }

    fun removeWaringView() {
        visibility = GONE
    }

    fun setTextAndMakeVisibleWithTimeout(
        text: String?,
        secondsRemoveAfter: Int? = DEFAULT_TIME_SECONDS_BEFORE_MESSAGE_IS_REMOVED
    ) {
        countDownTimer?.cancel()

        waringTextView.text = text
        visibility = VISIBLE

        if (secondsRemoveAfter != null && secondsRemoveAfter <= MAX_TIME_SECONDS_BEFORE_MESSAGE_IS_REMOVED) {
            countDownTimer = object : CountDownTimer(secondsRemoveAfter * 1000L, 1000) {
                override fun onTick(p0: Long) {}
                override fun onFinish() {
                    visibility = View.GONE
                }
            }

            (countDownTimer as CountDownTimer).start()
        }
    }

    fun setTextAndMakeVisible(text: String?) {
        countDownTimer?.cancel()

        waringTextView.text = text
        visibility = VISIBLE
    }
}