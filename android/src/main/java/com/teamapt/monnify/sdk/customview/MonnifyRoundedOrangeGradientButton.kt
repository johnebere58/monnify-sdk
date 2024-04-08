package com.teamapt.monnify.sdk.customview

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.monnify.monnify_payment_sdk.R

class MonnifyRoundedOrangeGradientButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    enum class ButtonState(val value: Int) {
        DISABLED(value = 0),
        ENABLED(value = 1),
        LOADING(value = 2);

        companion object {
            private val map = values().associateBy(ButtonState::value)

            fun fromInt(value: Int) = map[value]
        }
    }

    private val button: AppCompatButton

    private val loadingIndicator: MonnifyProcessingIndicatorView

    private var listener: OnClickListener? = null

    var text: CharSequence
        get() = button.text
        set(value) {
            button.text = value
        }


    var state: ButtonState = ButtonState.ENABLED
        set(value) {
            field = value
            setButtonState(field)
        }

    init {
        inflate(context, R.layout.plugin_monnify_rounded_orange_gradient_button, this)

        button = findViewById(R.id.primaryButton)
        loadingIndicator = findViewById(R.id.progressIndicator)

        context.obtainStyledAttributes(attrs, R.styleable.MonnifyRoundedOrangeGradientButton)
            .apply {
                try {

                    val text: CharSequence? =
                        getText(R.styleable.MonnifyRoundedOrangeGradientButton_android_text)
                    val state: Int = getInt(
                        R.styleable.MonnifyRoundedOrangeGradientButton_monnifyButtonState,
                        ButtonState.ENABLED.value
                    )

                    setViewProperties(text = text, state = state)
                } finally {
                    recycle()
                }
            }
    }

    private fun setViewProperties(text: CharSequence?, state: Int) {
        this.text = text ?: ""
        this.state = ButtonState.fromInt(state)!!
    }

    private fun setButtonState(state: ButtonState) {

        when (state) {
            ButtonState.DISABLED -> {
                button.isClickable = false
                button.isEnabled = false
                isEnabled = false
                loadingIndicator.visibility = GONE
                loadingIndicator.end()
            }
            ButtonState.ENABLED -> {
                button.isClickable = true
                button.isEnabled = true
                isEnabled = true
                loadingIndicator.visibility = GONE
                loadingIndicator.end()
            }
            ButtonState.LOADING -> {
                button.isClickable = false
                button.isEnabled = true
                isEnabled = false
                loadingIndicator.visibility = VISIBLE
                loadingIndicator.start()
            }
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        this.listener = l
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP && listener != null) {
            listener!!.onClick(this)
        }
        return super.dispatchTouchEvent(event)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_UP &&
            (event.keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
                    event.keyCode == KeyEvent.KEYCODE_ENTER) &&
            listener != null
        ) {
            listener!!.onClick(this)
        }
        return super.dispatchKeyEvent(event)
    }
}