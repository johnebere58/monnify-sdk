package com.teamapt.monnify.sdk.customview

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.daasuu.bl.BubbleLayout
import com.daasuu.bl.BubblePopupHelper
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.monnify.monnify_payment_sdk.R
import com.tomergoldst.tooltips.ToolTip
import com.tomergoldst.tooltips.ToolTipsManager

class MonnifyExpiryDateAndCVVInputView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {


    var expiryDate: CharSequence
        get() = expiryDateInputEditText.text.toString()
        set(value) = expiryDateInputEditText.setText(value)

    var cvv: CharSequence
        get() = cvvInputEditText.text.toString()
        set(value) = cvvInputEditText.setText(value)


    private val cvvTextInputLayout: TextInputLayout
    private val cvvInputEditText: TextInputEditText
    private val cvvRightIconImageView: AppCompatImageView
    private val cvvStatusMessageTextView: AppCompatTextView

    private val expiryDateTextInputLayout: TextInputLayout
    private val expiryDateInputEditText: TextInputEditText
    private val expiryDateStatusMessageTextView: AppCompatTextView


    interface ExpiryDateAndCVVInputEventListener {
        fun onCVVChanged(cvv: String)

        fun onExpiryDateChanged(expiryDate: String)
    }


    var eventListener: ExpiryDateAndCVVInputEventListener? = null


    init {
        inflate(context, R.layout.plugin_expiry_and_cvv_input_view, this)

        expiryDateTextInputLayout = findViewById(R.id.expiryDateTextInputLayout)
        expiryDateInputEditText = findViewById(R.id.expiryDateTextInputEditText)
        expiryDateStatusMessageTextView = findViewById(R.id.expiryDateStatusMessageTextView)

        cvvTextInputLayout = findViewById(R.id.cvvTextInputLayout)
        cvvInputEditText = findViewById(R.id.cvvTextInputEditText)
        cvvRightIconImageView = findViewById(R.id.cvvRightIconImageView)
        cvvStatusMessageTextView = findViewById(R.id.cvvStatusMessageTextView)

        setupView()
    }

    override fun hasFocus(): Boolean {
        return super.hasFocus() || expiryDateInputEditText.hasFocus() || cvvInputEditText.hasFocus()
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cvvInputEditText.letterSpacing = 0.1f
            expiryDateInputEditText.letterSpacing = 0.1f
        }

        addTextChangeListeners()
    }

    private fun addTextChangeListeners() {

        cvvInputEditText.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {
                cvvInputEditText.hint = context.getString(R.string.e_g_999)
            } else {
                cvvInputEditText.hint = ""
            }
        }

        expiryDateInputEditText.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {
                expiryDateInputEditText.hint = context.getString(R.string.mm_yy)
            } else {
                expiryDateInputEditText.hint = ""
            }
        }

        expiryDateInputEditText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val text = s.toString()
                val len = text.length

                if (before == 0 && len == 2) {
                    expiryDate = "$text/"
                    expiryDateInputEditText.setSelection(len + 1)
                }

                eventListener?.onExpiryDateChanged(text)
            }

            override fun afterTextChanged(s: Editable?) {
                //
            }

        })

        cvvInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                eventListener?.onCVVChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun _setupCVVToolTip(viewGroup: ViewGroup) {

        val toolTipsManager = ToolTipsManager()

        val builder = ToolTip.Builder(
            context, cvvRightIconImageView, viewGroup,
            resources.getString(R.string.cvv_tool_tip_text), ToolTip.POSITION_ABOVE
        )
        builder.setAlign(ToolTip.ALIGN_RIGHT)
        builder.setBackgroundColor(ContextCompat.getColor(context, R.color.monnifyLightBlue))
        builder.setGravity(ToolTip.GRAVITY_RIGHT)
        builder.setTextAppearance(R.style.TooltipTextAppearance)

        cvvRightIconImageView.setOnClickListener {
            toolTipsManager.findAndDismiss(cvvRightIconImageView)
            toolTipsManager.show(builder.build())
            Handler(Looper.getMainLooper()).postDelayed({
                toolTipsManager.findAndDismiss(cvvRightIconImageView)
            }, 1500)

        }
    }

    fun setupCVVToolTip() {
        val bubbleLayout: BubbleLayout = LayoutInflater.from(context)
            .inflate(R.layout.plugin_monnify_tooltip, null) as BubbleLayout

        val instructionTip: AppCompatTextView =
            bubbleLayout.findViewById(R.id.monnifyTooltipInstruction)

        instructionTip.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(
                context.resources.getString(R.string.your_cvv_is_the_3_digit_number_on_the_back_of_your_card),
                Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL
            )
        else
            Html.fromHtml(context.resources.getString(R.string.your_cvv_is_the_3_digit_number_on_the_back_of_your_card))

        val popupWindow = BubblePopupHelper.create(context, bubbleLayout)

        bubbleLayout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)

        val bubbleWidth = bubbleLayout.measuredWidth
        val bubbleHeight = bubbleLayout.measuredHeight

        cvvRightIconImageView.setOnClickListener {
            val location = IntArray(2)
            it.getLocationInWindow(location)

            val viewHeight = it.measuredHeight

            popupWindow.showAtLocation(
                it,
                Gravity.NO_GRAVITY,
                location[0] - bubbleWidth,
                location[1] + viewHeight / 2 - bubbleHeight / 2
            )
        }
    }

    fun setExpiryDateError(errorMessage: String?) {
        expiryDateStatusMessageTextView.visibility = View.VISIBLE
        expiryDateStatusMessageTextView.text = errorMessage
    }

    fun setCVVError(errorMessage: String?) {
        cvvStatusMessageTextView.visibility = View.VISIBLE
        cvvStatusMessageTextView.text = errorMessage
    }

}