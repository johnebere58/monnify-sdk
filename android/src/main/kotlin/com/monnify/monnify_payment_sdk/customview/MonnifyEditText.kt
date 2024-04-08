package com.monnify.monnify_payment_sdk.customview

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.material.textfield.TextInputLayout
import com.monnify.monnify_payment_sdk.R

class MonnifyEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayoutCompat(context, attrs) {

    var text: CharSequence
        get() = inputEditText.text.toString()
        set(value) = inputEditText.setText(value)

    private val inputBackgroundLayout: LinearLayoutCompat
    private val textInputLayout: TextInputLayout
    private val inputEditText: AppCompatEditText
    private val statusMessageTextView: AppCompatTextView
    private val inputImageView: AppCompatImageView

    private var allowClick: Boolean
    private var labelText: String
    private var hintText: String
    private val icon: Int
    private val errorMessage: String
    private var maxLength: Int
    private val showError: Boolean
    private val liveValidation: Boolean


    private var listener: OnClickListener? = null

    interface MonnifyEditTextInputEventListener {
        fun onViewLoseFocus()
        fun onTextChanged(text: CharSequence)
        fun onValidate(text: CharSequence?): Boolean
    }

    var eventListener: MonnifyEditTextInputEventListener? = null
        set(value) {
            field = value
            if (value != null) {
                inputEditText.addTextChangedListener(MonnifyEditTextTextWatcher(value))
            }
        }


    init {
        inflate(context, R.layout.plugin_monnify_edit_text, this)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MonnifyEditText)

        typedArray.apply {
            try {
                allowClick = getBoolean(R.styleable.MonnifyEditText_monnifyEditTextClickable, false)
                labelText = getString(R.styleable.MonnifyEditText_monnifyEditTextLabelText) ?: ""
                hintText = getString(R.styleable.MonnifyEditText_monnifyEditTextHintText) ?: ""
                icon = getResourceId(
                    R.styleable.MonnifyEditText_monnifyEditTextIcon,
                    0
                )
                errorMessage =
                    getString(R.styleable.MonnifyEditText_monnifyEditTextErrorMessage) ?: ""
                maxLength = getInteger(R.styleable.MonnifyEditText_monnifyEditTextMaxLength, 0)
                showError =
                    getBoolean(R.styleable.MonnifyEditText_monnifyEditTextShowError, true)
                liveValidation =
                    getBoolean(R.styleable.MonnifyEditText_monnifyEditTextLiveValidation, false)
            } finally {
                recycle()
            }
        }

        inputBackgroundLayout = findViewById(R.id.inputBackgroundLayout)
        textInputLayout = findViewById(R.id.textInputLayout)
        inputEditText = findViewById(R.id.inputEditText)
        statusMessageTextView = findViewById(R.id.statusMessageTextView)
        inputImageView = findViewById(R.id.inputImageView)

        setupView()
    }


    private fun setupView() {
        textInputLayout.hint = labelText
        inputEditText.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {
                inputEditText.hint = hintText
                statusMessageTextView.visibility = GONE
            } else {
                inputEditText.hint = ""
                eventListener?.onViewLoseFocus()
            }
        }

        if (allowClick) {
            inputEditText.isCursorVisible = false
            inputEditText.isFocusable = false
            inputEditText.inputType = InputType.TYPE_NULL
        }

        if (icon != 0) {
            inputImageView.visibility = VISIBLE
            inputImageView.setImageResource(icon)
        }
        if (maxLength > 0) {
            inputEditText.filters = arrayOf(InputFilter.LengthFilter(maxLength))
        }
    }

    fun disable() {
        inputEditText.isEnabled = false
        inputEditText.isFocusable = false
        inputEditText.inputType = InputType.TYPE_NULL
    }

    fun clear() {
        setError(null)
        inputEditText.text?.clear()
    }

    private fun setError(errorMessage: String?) {
        statusMessageTextView.visibility = if (errorMessage.isNullOrEmpty()) GONE else VISIBLE
        statusMessageTextView.text = errorMessage
    }

    fun isNotEmpty(): Boolean {
        return text.isNotEmpty() || text.isNotBlank()
    }

    fun validate(): Boolean {
        val isValid: Boolean = eventListener?.onValidate(inputEditText.text) ?: false

        if (!isValid) {
            setError(errorMessage)
        }

        return isValid
    }

    private inner class MonnifyEditTextTextWatcher(private val eventListener: MonnifyEditTextInputEventListener) :
        TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            //
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (liveValidation) {
                validate()
            }
            eventListener.onTextChanged(s)
        }

        override fun afterTextChanged(s: Editable) {
            //
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