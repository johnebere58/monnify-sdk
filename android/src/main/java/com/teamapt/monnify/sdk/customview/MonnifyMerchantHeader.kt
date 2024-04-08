package com.teamapt.monnify.sdk.customview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Base64
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.contains
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import com.squareup.picasso.Picasso
import com.teamapt.monnify.sdk.R
import com.teamapt.monnify.sdk.data.RoundedImageTransform
import com.teamapt.monnify.sdk.util.Logger
import com.teamapt.monnify.sdk.util.SdkUtils
import kotlinx.coroutines.*
import java.io.IOException
import kotlin.math.roundToInt

@Suppress("DEPRECATION")
class MonnifyMerchantHeader @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val topCornerRadius: Int
    private val gapSize: Int

    private val topPadding: Int
    private val bottomPadding: Int
    private val startPadding: Int
    private val endPadding: Int

    private val background: Int

    private var totalAmountLabelText: String
    private val totalAmountLabelTextSize: Int
    private val totalAmountLabelTextColor: Int

    private var totalAmountText: String
    private val totalAmountTextSize: Int
    private val totalAmountTextColor: Int

    private val contentBackgroundColor: Int
    private val contentBackgroundCornerRadius: Int
    private val contentTopPadding: Int
    private val contentBottomPadding: Int
    private val contentStartPadding: Int
    private val contentEndPadding: Int

    private var amountText: String
    private val amountLabelText: String
    private var feeText: String
    private val feeLabelText: String
    private val amountAndFeeTextSize: Int
    private val amountAndFeeTextColor: Int

    private val actionBarCancelText: String
    private val actionBarCancelIcon: Int
    private val actionBarCancelIconSize: Int
    private val actionBarTextColor: Int
    private val actionBarTextSize: Int
    private val actionBarTopPadding: Int
    private val actionBarBottomPadding: Int
    private val actionBarStartPadding: Int
    private val actionBarEndPadding: Int

    private var merchantLogo: Bitmap? = null

    private var emailAddressText: String
    private val emailAddressTextSize: Int
    private val emailAddressTextColor: Int

    private val merchantLogoRadius: Int
    private val merchantLogoBorderWidth: Int
    private val merchantLogoBackground: Int
    private val merchantLogoBorderColor: Int

    private var merchantNameText: String
    private val merchantNameTextSize: Int
    private val merchantNameTextColor: Int

    private lateinit var emailAddressTextLayout: StaticLayout
    private lateinit var cancelTextLayout: StaticLayout
    private lateinit var totalAmountLabelTextLayout: StaticLayout
    private lateinit var totalAmountTextLayout: StaticLayout
    private lateinit var merchantNameLayout: StaticLayout
    private lateinit var amountTextLayout: StaticLayout
    private lateinit var amountLabelTextLayout: StaticLayout
    private lateinit var feeTextLayout: StaticLayout
    private lateinit var feeLabelTextLayout: StaticLayout

    private var cancelTextRegion: RectF? = null

    private val cancelTextListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return true
        }
    }

    fun setOnCancelClickListener(l: OnClickListener?) {
        cancelListener = l
    }

    private val cancelTextDetector: GestureDetector = GestureDetector(context, cancelTextListener)

    private var cancelListener: OnClickListener? = null

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {

        if (isInEditMode)
            setBackgroundColor(ContextCompat.getColor(context, R.color.monnifyActivityBackground))


        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.MonnifyMerchantHeader)

        typedArray.apply {

            try {
                topCornerRadius = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerCornerRadius,
                    context.resources.getDimensionPixelSize(R.dimen.dimen16dp)
                )
                gapSize = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerGapSize,
                    context.resources.getDimensionPixelSize(R.dimen.dimen8dp)
                )

                topPadding = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerTopPadding,
                    context.resources.getDimensionPixelSize(R.dimen.dimen16dp)
                )

                bottomPadding = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerBottomPadding,
                    context.resources.getDimensionPixelSize(R.dimen.dimen16dp)
                )

                startPadding = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerStartPadding,
                    context.resources.getDimensionPixelSize(R.dimen.dimen20dp)
                )

                endPadding = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerEndPadding,
                    context.resources.getDimensionPixelSize(R.dimen.dimen20dp)
                )

                background = getResourceId(
                    R.styleable.MonnifyMerchantHeader_headerBackground,
                    R.color.monnifyWhite
                )

                totalAmountLabelText = getString(
                    R.styleable.MonnifyMerchantHeader_headerTotalAmountLabelText
                ) ?: context.resources.getString(R.string.amount_to_pay)

                totalAmountLabelTextSize = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerTotalAmountLabelTextSize,
                    context.resources.getDimensionPixelSize(R.dimen.dimen10sp)
                )
                totalAmountLabelTextColor =
                    getColor(
                        R.styleable.MonnifyMerchantHeader_headerTotalAmountLabelTextColor,
                        ContextCompat.getColor(context, R.color.monnifyDarkGreen)
                    )

                totalAmountText = getString(
                    R.styleable.MonnifyMerchantHeader_headerTotalAmountText
                ) ?: context.resources.getString(R.string.default_amount)

                totalAmountTextSize = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerTotalAmountTextSize,
                    context.resources.getDimensionPixelSize(R.dimen.dimen22sp)
                )
                totalAmountTextColor = getColor(
                    R.styleable.MonnifyMerchantHeader_headerTotalAmountTextColor,
                    ContextCompat.getColor(context, R.color.monnifyDarkGreen)
                )

                contentBackgroundColor =
                    getColor(
                        R.styleable.MonnifyMerchantHeader_headerContentBackgroundColor,
                        ContextCompat.getColor(context, R.color.monnifyBackgroundSym)
                    )

                contentBackgroundCornerRadius = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerContentBackgroundCornerRadius,
                    context.resources.getDimensionPixelSize(R.dimen.dimen8dp)
                )

                contentTopPadding = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerContentTopPadding,
                    context.resources.getDimensionPixelSize(R.dimen.dimen8dp)
                )

                contentBottomPadding = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerContentBottomPadding,
                    context.resources.getDimensionPixelSize(R.dimen.dimen8dp)
                )

                contentStartPadding = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerContentStartPadding,
                    context.resources.getDimensionPixelSize(R.dimen.dimen12dp)
                )

                contentEndPadding = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerContentEndPadding,
                    context.resources.getDimensionPixelSize(R.dimen.dimen12dp)
                )

                emailAddressText =
                    getString(R.styleable.MonnifyMerchantHeader_headerEmailAddressText)
                        ?: if (isInEditMode) context.resources.getString(R.string.hint_customer_email) else ""

                emailAddressTextSize =
                    getDimensionPixelSize(
                        R.styleable.MonnifyMerchantHeader_headerEmailAddressTextSize,
                        context.resources.getDimensionPixelSize(R.dimen.dimen12sp)
                    )

                emailAddressTextColor = getColor(
                    R.styleable.MonnifyMerchantHeader_headerEmailAddressTextColor,
                    ContextCompat.getColor(context, R.color.monnifyNeutralBlackTrans50)
                )

                actionBarCancelText =
                    getString(R.styleable.MonnifyMerchantHeader_headerActionBarCancelText)
                        ?: context.resources.getString(R.string.close_cap)

                actionBarCancelIcon = getResourceId(
                    R.styleable.MonnifyMerchantHeader_headerActionBarCancelIcon,
                    R.drawable.ic_close
                )

                actionBarCancelIconSize = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerActionBarCancelIconSize,
                    context.resources.getDimensionPixelSize(R.dimen.dimen12dp)
                )

                actionBarTextSize = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerActionBarTextSize,
                    context.resources.getDimensionPixelSize(R.dimen.dimen12sp)
                )

                actionBarTextColor = getColor(
                    R.styleable.MonnifyMerchantHeader_headerActionBarTextColor,
                    ContextCompat.getColor(context, R.color.monnifySurfaceText)
                )

                actionBarTopPadding = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerActionBarTopPadding,
                    context.resources.getDimensionPixelSize(R.dimen.dimen27dp)
                )

                actionBarBottomPadding = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerActionBarBottomPadding,
                    context.resources.getDimensionPixelSize(R.dimen.dimen5dp)
                )

                actionBarStartPadding = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerActionBarStartPadding,
                    context.resources.getDimensionPixelSize(R.dimen.dimen20dp)
                )

                actionBarEndPadding = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerActionBarEndPadding,
                    context.resources.getDimensionPixelSize(R.dimen.dimen20dp)
                )

                merchantLogoRadius = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerMerchantLogoRadius,
                    context.resources.getDimensionPixelSize(R.dimen.merchantLogoRadius)
                )

                merchantLogoBorderWidth = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerMerchantLogoBorderWidth,
                    context.resources.getDimensionPixelSize(R.dimen.dimen4dp)
                )

                merchantLogoBorderColor = getColor(
                    R.styleable.MonnifyMerchantHeader_headerMerchantLogoBorderColor,
                    ContextCompat.getColor(context, R.color.monnifyWhite)
                )

                merchantLogoBackground = getResourceId(
                    R.styleable.MonnifyMerchantHeader_headerMerchantLogoBackground,
                    R.drawable.bg_monnify_logo
                )

                merchantNameText =
                    getString(R.styleable.MonnifyMerchantHeader_headerMerchantNameText)
                        ?: if (isInEditMode) context.resources.getString(R.string.hint_merchant_name) else ""

                merchantNameTextSize = getDimensionPixelSize(
                    R.styleable.MonnifyMerchantHeader_headerMerchantNameTextSize,
                    context.resources.getDimensionPixelSize(R.dimen.dimen12sp)
                )

                merchantNameTextColor = getColor(
                    R.styleable.MonnifyMerchantHeader_headerMerchantNameTextColor,
                    ContextCompat.getColor(context, R.color.monnifyNeutralBlackTrans50)
                )

                amountText = getString(R.styleable.MonnifyMerchantHeader_headerAmountText)
                    ?: context.resources.getString(R.string.default_amount)
                amountLabelText = getString(R.styleable.MonnifyMerchantHeader_headerAmountLabelText)
                    ?: context.getString(R.string.amount)
                feeText = getString(R.styleable.MonnifyMerchantHeader_headerFeeText)
                    ?: context.resources.getString(R.string.default_amount)
                feeLabelText = getString(R.styleable.MonnifyMerchantHeader_headerFeeLayoutText)
                    ?: context.resources.getString(R.string.fee)
                amountAndFeeTextSize =
                    getDimensionPixelSize(
                        R.styleable.MonnifyMerchantHeader_headerAmountAndFeeTextSize,
                        context.resources.getDimensionPixelSize(R.dimen.dimen9sp)
                    )
                amountAndFeeTextColor = getColor(
                    R.styleable.MonnifyMerchantHeader_headerAmountAndFeeTextColor,
                    ContextCompat.getColor(context, R.color.monnifyAmountAndFeeText)
                )
            } finally {
                recycle()
            }
        }
    }

    private var actionBarHeight = 0
    private var contentHeight = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width: Int = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            widthSize + ViewCompat.getPaddingEnd(this) + ViewCompat.getPaddingEnd(this)
        }

        setupEmailText(width = width)
        setupCancelText()
        setupTotalAmountLabelText()
        setupTotalAmountText()
        setupAmountLabelText()
        setupAmountText()
        setupFeeLabelText()
        setupFeeText()
        setupMerchantText(width = width)

        actionBarHeight =
            cancelTextLayout.height + actionBarTopPadding + actionBarBottomPadding

        contentHeight =
            topPadding + (merchantLogoBorderWidth + merchantLogoRadius) * 2 + gapSize + contentTopPadding + totalAmountLabelTextLayout.height + gapSize + totalAmountTextLayout.height + getDividerTopMargin() + getDividerHeight() + getDividerBottomMargin() + amountLabelTextLayout.height + contentBottomPadding + bottomPadding

        val height: Int = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            if (heightMode == MeasureSpec.AT_MOST)
                heightSize.coerceAtMost(contentHeight + actionBarHeight)
            else
                contentHeight + actionBarHeight
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        if (canvas != null) {
            canvas.save()
            drawCancelText(canvas)
            drawCurveCorner(canvas)
            drawBackground(canvas)
            drawMerchantLogo(canvas)
            drawMerchantName(canvas)
            drawEmailAddressText(canvas)
            drawContentBackground(canvas)
            drawTotalAmountLabelText(canvas)
            drawTotalAmountText(canvas)
            drawContentHorizontalDivider(canvas)
            drawAmountLabelText(canvas)
            drawAmountText(canvas)
            drawAmountFeeVerticalDivider(canvas)
            drawFeeLabelText(canvas)
            drawFeeText(canvas)

            canvas.restore()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return cancelTextDetector.onTouchEvent(event).let {
            if (!it) {
                if (event.action == MotionEvent.ACTION_UP) {
                    if (cancelTextRegion != null && cancelTextRegion!!.contains(
                            PointF(
                                event.x,
                                event.y
                            )
                        )
                    ) {
                        performClick()
                    }
                }
                false
            } else true
        }
    }

    override fun performClick(): Boolean {
        if (cancelListener != null) {
            cancelListener!!.onClick(this)
            return true
        }
        return super.performClick()
    }

    override fun onDetachedFromWindow() {
        scope.cancel()
        super.onDetachedFromWindow()
    }

    private fun setupEmailText(width: Int) {
        val emailTextPaint: TextPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = emailAddressTextSize * 1f
            color = emailAddressTextColor
            typeface =
                if (isInEditMode) null else ResourcesCompat.getFont(context, R.font.inter_regular)
        }

        val textWidth: Float = emailTextPaint.measureText(emailAddressText)

        val ellipsizeWidth: Int = (width * 0.50 - endPadding - endPadding / 2).toInt()


        emailAddressTextLayout =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(
                    emailAddressText,
                    0,
                    emailAddressText.length,
                    emailTextPaint,
                    textWidth.roundToInt()
                ).apply {
                    setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    setIncludePad(false)
                    setMaxLines(1)
                    setLineSpacing(0f, 1f)
                    setEllipsize(TextUtils.TruncateAt.END)
                    setEllipsizedWidth(ellipsizeWidth)
                }.build()
            } else {
                StaticLayout(
                    emailAddressText,
                    0,
                    emailAddressText.length,
                    emailTextPaint,
                    textWidth.roundToInt(),
                    Layout.Alignment.ALIGN_NORMAL,
                    1f,
                    0f,
                    false,
                    TextUtils.TruncateAt.END,
                    ellipsizeWidth
                )
            }
    }

    private fun setupMerchantText(width: Int) {
        val merchantNameTextPaint: TextPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = merchantNameTextSize * 1f
            color = merchantNameTextColor
            typeface =
                if (isInEditMode) null else ResourcesCompat.getFont(context, R.font.inter_bold)
            isFakeBoldText = true
        }

        val textWidth: Float = width * 0.20f

        val ellipsizeWidth: Int = (textWidth).toInt()


        merchantNameLayout =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(
                    merchantNameText,
                    0,
                    merchantNameText.length,
                    merchantNameTextPaint,
                    textWidth.roundToInt()
                ).apply {
                    setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    setIncludePad(false)
                    setMaxLines(1)
                    setLineSpacing(0f, 1f)
                    setEllipsize(TextUtils.TruncateAt.END)
                    setEllipsizedWidth(ellipsizeWidth)
                }.build()
            } else {
                StaticLayout(
                    merchantNameText,
                    0,
                    merchantNameText.length,
                    merchantNameTextPaint,
                    textWidth.roundToInt(),
                    Layout.Alignment.ALIGN_NORMAL,
                    1f,
                    0f,
                    false,
                    TextUtils.TruncateAt.END,
                    ellipsizeWidth
                )
            }
    }

    private fun setupCancelText() {
        val cancelTextPaint: TextPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = actionBarTextSize * 1f
            color = actionBarTextColor
            typeface =
                if (isInEditMode) null else ResourcesCompat.getFont(context, R.font.rubik_regular)
        }

        val textWidth: Float = cancelTextPaint.measureText(actionBarCancelText)

        cancelTextLayout =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(
                    actionBarCancelText,
                    0,
                    actionBarCancelText.length,
                    cancelTextPaint,
                    textWidth.roundToInt()
                ).apply {
                    setAlignment(Layout.Alignment.ALIGN_CENTER)
                    setIncludePad(false)
                    setMaxLines(1)
                    setLineSpacing(0f, 1f)
                }.build()
            } else {
                StaticLayout(
                    actionBarCancelText,
                    0,
                    actionBarCancelText.length,
                    cancelTextPaint,
                    textWidth.roundToInt(),
                    Layout.Alignment.ALIGN_CENTER,
                    1f,
                    0f,
                    false
                )
            }
    }

    private fun setupTotalAmountLabelText() {
        val labelTextPaint: TextPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = totalAmountLabelTextSize * 1f
            color = totalAmountLabelTextColor
            typeface =
                if (isInEditMode) null else ResourcesCompat.getFont(context, R.font.inter_regular)
        }

        val textWidth: Float = labelTextPaint.measureText(totalAmountLabelText)

        totalAmountLabelTextLayout =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(
                    totalAmountLabelText,
                    0,
                    totalAmountLabelText.length,
                    labelTextPaint,
                    textWidth.roundToInt()
                ).apply {
                    setAlignment(Layout.Alignment.ALIGN_CENTER)
                    setIncludePad(false)
                    setMaxLines(1)
                    setLineSpacing(0f, 1f)
                }.build()
            } else {
                StaticLayout(
                    totalAmountLabelText,
                    0,
                    totalAmountLabelText.length,
                    labelTextPaint,
                    textWidth.roundToInt(),
                    Layout.Alignment.ALIGN_CENTER,
                    1f,
                    0f,
                    false
                )
            }
    }

    private fun setupTotalAmountText() {
        val textPaint: TextPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = totalAmountTextSize * 1f
            color = totalAmountTextColor
            typeface =
                if (isInEditMode) null else ResourcesCompat.getFont(context, R.font.inter_bold)
            isFakeBoldText = true
        }

        val textWidth: Float = textPaint.measureText(totalAmountText)

        totalAmountTextLayout =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(
                    totalAmountText,
                    0,
                    totalAmountText.length,
                    textPaint,
                    textWidth.roundToInt()
                ).apply {
                    setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    setIncludePad(false)
                    setMaxLines(1)
                    setLineSpacing(0f, 1f)
                }.build()
            } else {
                StaticLayout(
                    totalAmountText,
                    0,
                    totalAmountText.length,
                    textPaint,
                    textWidth.roundToInt(),
                    Layout.Alignment.ALIGN_NORMAL,
                    1f,
                    0f,
                    false
                )
            }
    }

    private fun setupAmountLabelText() {
        val textPaint: TextPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = amountAndFeeTextSize * 1f
            color = amountAndFeeTextColor
            typeface =
                if (isInEditMode) null else ResourcesCompat.getFont(context, R.font.inter_regular)
        }

        val textWidth: Float = textPaint.measureText(amountLabelText)

        amountLabelTextLayout =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(
                    amountLabelText,
                    0,
                    amountLabelText.length,
                    textPaint,
                    textWidth.roundToInt()
                ).apply {
                    setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    setIncludePad(false)
                    setMaxLines(1)
                    setLineSpacing(0f, 1f)
                }.build()
            } else {
                StaticLayout(
                    amountLabelText,
                    0,
                    amountLabelText.length,
                    textPaint,
                    textWidth.roundToInt(),
                    Layout.Alignment.ALIGN_NORMAL,
                    1f,
                    0f,
                    false
                )
            }
    }

    private fun setupAmountText() {
        val textPaint: TextPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = amountAndFeeTextSize * 1f
            color = amountAndFeeTextColor
            typeface =
                if (isInEditMode) null else ResourcesCompat.getFont(context, R.font.inter_bold)
            isFakeBoldText = true
        }

        val textWidth: Float = textPaint.measureText(amountText)

        amountTextLayout =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(
                    amountText,
                    0,
                    amountText.length,
                    textPaint,
                    textWidth.roundToInt()
                ).apply {
                    setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    setIncludePad(false)
                    setMaxLines(1)
                    setLineSpacing(0f, 1f)
                }.build()
            } else {
                StaticLayout(
                    amountText,
                    0,
                    amountText.length,
                    textPaint,
                    textWidth.roundToInt(),
                    Layout.Alignment.ALIGN_NORMAL,
                    1f,
                    0f,
                    false
                )
            }
    }

    private fun setupFeeLabelText() {
        val textPaint: TextPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = amountAndFeeTextSize * 1f
            color = amountAndFeeTextColor
            typeface =
                if (isInEditMode) null else ResourcesCompat.getFont(context, R.font.inter_regular)
        }

        val textWidth: Float = textPaint.measureText(feeLabelText)

        feeLabelTextLayout =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(
                    feeLabelText,
                    0,
                    feeLabelText.length,
                    textPaint,
                    textWidth.roundToInt()
                ).apply {
                    setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    setIncludePad(false)
                    setMaxLines(1)
                    setLineSpacing(0f, 1f)
                }.build()
            } else {
                StaticLayout(
                    feeLabelText,
                    0,
                    feeLabelText.length,
                    textPaint,
                    textWidth.roundToInt(),
                    Layout.Alignment.ALIGN_NORMAL,
                    1f,
                    0f,
                    false
                )
            }
    }

    private fun setupFeeText() {
        val textPaint: TextPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = amountAndFeeTextSize * 1f
            color = amountAndFeeTextColor
            typeface =
                if (isInEditMode) null else ResourcesCompat.getFont(context, R.font.inter_bold)
            isFakeBoldText = true
        }

        val textWidth: Float = textPaint.measureText(feeText)

        feeTextLayout =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(
                    feeText,
                    0,
                    feeText.length,
                    textPaint,
                    textWidth.roundToInt()
                ).apply {
                    setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    setIncludePad(false)
                    setMaxLines(1)
                    setLineSpacing(0f, 1f)
                }.build()
            } else {
                StaticLayout(
                    feeText,
                    0,
                    feeText.length,
                    textPaint,
                    textWidth.roundToInt(),
                    Layout.Alignment.ALIGN_NORMAL,
                    1f,
                    0f,
                    false
                )
            }
    }

    private fun getMerchangeNameLogoStaticLayout(text: String): StaticLayout {
        val textPaint: TextPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = resources.getDimension(R.dimen.dimen24sp)
            color = ContextCompat.getColor(context, R.color.monnifyWhite)
            typeface =
                if (isInEditMode) null else ResourcesCompat.getFont(context, R.font.rubik_bold)
            isFakeBoldText = true
        }

        val textWidth: Float = textPaint.measureText(text)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(
                text,
                0,
                text.length,
                textPaint,
                textWidth.roundToInt()
            ).apply {
                setAlignment(Layout.Alignment.ALIGN_NORMAL)
                setIncludePad(false)
                setMaxLines(1)
                setLineSpacing(0f, 1f)
            }.build()
        } else {
            StaticLayout(
                text,
                0,
                text.length,
                textPaint,
                textWidth.roundToInt(),
                Layout.Alignment.ALIGN_NORMAL,
                1f,
                0f,
                false
            )
        }
    }

    private fun drawEmailAddressText(canvas: Canvas) {
        canvas.save()

        val dx: Float = width - emailAddressTextLayout.width - endPadding * 1f
        val dy: Float =
            actionBarHeight + topPadding + merchantLogoBorderWidth + merchantLogoRadius - emailAddressTextLayout.height / 2f

        canvas.translate(dx, dy)
        emailAddressTextLayout.draw(canvas)

        canvas.restore()
    }

    private fun drawCancelText(canvas: Canvas) {
        canvas.save()

        val dx: Float =
            width - actionBarEndPadding - actionBarCancelIconSize - actionBarCancelIconSize * 0.25f - cancelTextLayout.width * 1f
        val dy: Float = actionBarTopPadding * 1f

        canvas.translate(dx, dy)
        cancelTextLayout.draw(canvas)

        val bitmap = ContextCompat.getDrawable(context, actionBarCancelIcon)!!.toBitmap()
        val bitmapDrawable = BitmapDrawable(context.resources, bitmap)

        val left: Int =
            (cancelTextLayout.width + actionBarCancelIconSize * 0.25).toInt()
        val top: Int = (cancelTextLayout.height * 0.10).toInt()


        bitmapDrawable.setBounds(
            left,
            top,
            left + actionBarCancelIconSize,
            top + actionBarCancelIconSize
        )

        bitmapDrawable.draw(canvas)

        cancelTextRegion = RectF(
            dx,
            dy,
            dx + left + actionBarCancelIconSize,
            dy + actionBarTextSize,
        )

        canvas.restore()
    }

    private fun drawTotalAmountLabelText(canvas: Canvas) {

        canvas.save()

        val dx: Float = startPadding + contentStartPadding * 1f
        val dy: Float =
            actionBarHeight + topPadding + (merchantLogoRadius + merchantLogoBorderWidth) * 2 + gapSize + contentTopPadding * 1f

        canvas.translate(dx, dy)
        totalAmountLabelTextLayout.draw(canvas)

        canvas.restore()
    }

    private fun drawTotalAmountText(canvas: Canvas) {
        canvas.save()

        val textTopMargin: Int = context.resources.getDimensionPixelSize(R.dimen.dimen4dp)

        val dx: Float = startPadding + contentStartPadding * 1f
        val dy: Float =
            actionBarHeight + topPadding + (merchantLogoRadius + merchantLogoBorderWidth) * 2 + gapSize + contentTopPadding + totalAmountLabelTextLayout.height + textTopMargin * 1f

        canvas.translate(dx, dy)
        totalAmountTextLayout.draw(canvas)

        canvas.restore()
    }


    private fun drawCurveCorner(canvas: Canvas) {
        val path: Path = Path().apply {
            moveTo(0f, height * 1f)
            lineTo(0f, actionBarHeight * 1f + topCornerRadius)

            arcTo(
                RectF(
                    0f,
                    actionBarHeight * 1f,
                    topCornerRadius * 2f,
                    actionBarHeight * 1f + topCornerRadius * 2
                ), 180f, 90f
            )

            lineTo(width * 1f - topCornerRadius, actionBarHeight * 1f)

            arcTo(
                RectF(
                    width * 1f - topCornerRadius * 2,
                    actionBarHeight * 1f,
                    width * 1f,
                    actionBarHeight * 1f + topCornerRadius * 2
                ), 270f, 90f
            )

            lineTo(width * 1f, height * 1f)

            lineTo(0f, height * 1f)

            close()
        }

        canvas.clipPath(path)

    }

    private fun drawContentHorizontalDivider(canvas: Canvas) {
        canvas.save()

        val dx: Float = startPadding + contentStartPadding * 1f
        val dy: Float = getDividerDisplacementFromTop()

        canvas.translate(dx, dy)

        val path = Path().apply {
            lineTo(getDividerWidth() * 1f, 0f)
            lineTo(getDividerWidth() * 1f, getDividerHeight() * 1f)
            lineTo(0f, getDividerHeight() * 1f)
            lineTo(0f, 0f)
        }

        val paint = Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, R.color.monnifyMerchantHeaderContentDivider)
        }

        canvas.drawPath(path, paint)

        canvas.restore()
    }

    private fun drawAmountLabelText(canvas: Canvas) {
        canvas.save()

        val dx: Float = startPadding + contentStartPadding * 1f
        val dy: Float = getDividerDisplacementFromTop() + getDividerBottomMargin()

        canvas.translate(dx, dy)

        amountLabelTextLayout.draw(canvas)

        canvas.restore()
    }

    private fun drawAmountText(canvas: Canvas) {
        canvas.save()

        val dx: Float =
            startPadding + contentStartPadding + amountLabelTextLayout.width + getGapBetweenLabelAndValue() * 1f
        val dy = getDividerDisplacementFromTop() + getDividerBottomMargin()

        canvas.translate(dx, dy)

        amountTextLayout.draw(canvas)


        canvas.restore()
    }

    private fun drawAmountFeeVerticalDivider(canvas: Canvas) {
        canvas.save()

        val dx: Float = getAmountTextExtent() + getDividerStartMargin() * 1f
        val dy = getDividerDisplacementFromTop() + getDividerBottomMargin()

        canvas.translate(dx, dy)

        val height: Int = amountLabelTextLayout.height

        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(getDividerHeight() * 1f, 0f)
            lineTo(getDividerHeight() * 1f, height * 1f)
            lineTo(0f, height * 1f)
            lineTo(0f, 0f)
            close()
        }

        val paint = Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, R.color.monnifyMerchantHeaderContentDivider)
        }

        canvas.drawPath(path, paint)

        canvas.restore()
    }

    private fun drawFeeLabelText(canvas: Canvas) {
        canvas.save()

        val dx: Float =
            getAmountTextExtent() + getDividerStartMargin() + getDividerHeight() + getDividerEndMargin() * 1f
        val dy: Float = getDividerDisplacementFromTop() + getDividerBottomMargin()

        canvas.translate(dx, dy)

        feeLabelTextLayout.draw(canvas)

        canvas.restore()
    }

    private fun drawFeeText(canvas: Canvas) {
        canvas.save()

        val dx: Float =
            getAmountTextExtent() + getDividerStartMargin() + getDividerHeight() + getDividerEndMargin() + feeLabelTextLayout.width + getGapBetweenLabelAndValue() * 1f
        val dy: Float = getDividerDisplacementFromTop() + getDividerBottomMargin()

        canvas.translate(dx, dy)

        feeTextLayout.draw(canvas)

        canvas.restore()
    }

    private fun drawBackground(canvas: Canvas) {

        val drawable: Drawable = ContextCompat.getDrawable(context, background)!!
        drawable.setBounds(0, actionBarHeight, width, height)

        drawable.draw(canvas)

    }

    private fun drawContentBackground(canvas: Canvas) {
        canvas.save()

        val contentHeight: Float = getContentHeight()

        val left: Float = startPadding * 1f
        val top: Float =
            actionBarHeight + topPadding + (merchantLogoBorderWidth + merchantLogoRadius) * 2 + gapSize * 1f
        val right: Float = width - endPadding * 1f
        val bottom: Float = top + contentHeight

        val path = Path().apply {
            addRoundRect(
                RectF(
                    left,
                    top,
                    right,
                    bottom
                ),
                contentBackgroundCornerRadius * 1f,
                contentBackgroundCornerRadius * 1f,
                Path.Direction.CW
            )
        }

        val paint = Paint().apply {
            style = Paint.Style.FILL
            color = contentBackgroundColor
        }


        canvas.drawPath(path, paint)

        canvas.restore()
    }

    private fun drawMerchantLogo(canvas: Canvas) {
        canvas.save()

        val cx: Float = startPadding + merchantLogoRadius + merchantLogoBorderWidth * 1f
        val cy: Float =
            actionBarHeight + topPadding + merchantLogoRadius + merchantLogoBorderWidth * 1f

        val outerCircleRadius: Float = merchantLogoRadius + merchantLogoBorderWidth * 1f

        val outerCirclePaint: Paint = Paint().apply {
            color = merchantLogoBorderColor
            isAntiAlias = true
        }

        canvas.drawCircle(cx, cy, outerCircleRadius, outerCirclePaint)

        val innerCircle = Path().apply {
            addCircle(cx, cy, merchantLogoRadius * 1f, Path.Direction.CW)
        }

        canvas.clipPath(innerCircle)

        val logo =
            if (merchantLogo == null) ContextCompat.getDrawable(
                context,
                merchantLogoBackground
            ) else BitmapDrawable(context.resources, merchantLogo)


        logo!!.bounds = Rect(
            (cx - merchantLogoRadius).toInt(),
            (cy - merchantLogoRadius).toInt(),
            (cx + merchantLogoRadius).toInt(),
            (cy + merchantLogoRadius).toInt()
        )

        logo.draw(canvas)

        canvas.restore()
    }

    private fun drawMerchantName(canvas: Canvas) {
        canvas.save()

        val dx: Float =
            startPadding + merchantLogoBorderWidth * 2 + merchantLogoRadius * 2 + merchantLogoBorderWidth * 1f
        val dy: Float =
            actionBarHeight + topPadding + merchantLogoBorderWidth + merchantLogoRadius - merchantNameLayout.height / 2f

        canvas.translate(dx, dy)

        merchantNameLayout.draw(canvas)

        canvas.restore()
    }

    private fun getAmountTextExtent(): Int {
        return startPadding + contentStartPadding + amountLabelTextLayout.width + getGapBetweenLabelAndValue() + amountTextLayout.width
    }

    private fun getGapBetweenLabelAndValue(): Int {
        return context.resources.getDimensionPixelSize(R.dimen.dimen2dp)
    }

    private fun getTotalAmountTextTopMargin(): Int {
        return context.resources.getDimensionPixelSize(R.dimen.dimen4dp)
    }

    private fun getDividerTopMargin(): Int {
        return context.resources.getDimensionPixelSize(R.dimen.dimen6dp)
    }

    private fun getDividerBottomMargin(): Int {
        return context.resources.getDimensionPixelSize(R.dimen.dimen10dp)
    }

    private fun getDividerStartMargin(): Int {
        return context.resources.getDimensionPixelSize(R.dimen.dimen6dp)
    }

    private fun getDividerEndMargin(): Int {
        return context.resources.getDimensionPixelSize(R.dimen.dimen6dp)
    }

    private fun getDividerHeight(): Int {
        return context.resources.getDimensionPixelSize(R.dimen.dimen1dp)
    }

    private fun getDividerWidth(): Int {
        return width - startPadding - contentStartPadding - endPadding - contentEndPadding
    }

    private fun getContentHeight(): Float {
        return contentTopPadding + contentBottomPadding + totalAmountLabelTextLayout.height + getTotalAmountTextTopMargin() + totalAmountTextLayout.height + getDividerTopMargin() + getDividerHeight() + getDividerBottomMargin() + amountLabelTextLayout.height * 1f
    }

    private fun getDividerDisplacementFromTop(): Float {
        return actionBarHeight + topPadding + (merchantLogoRadius + merchantLogoBorderWidth) * 2 + gapSize + contentTopPadding + totalAmountLabelTextLayout.height + getTotalAmountTextTopMargin() + totalAmountTextLayout.height + getDividerTopMargin() * 1f
    }


    fun loadMerchantLogo(url: String?) {
        if (url == null) {
            drawMerchantLogoFromName(name = merchantNameText)
        } else {
            if (SdkUtils.isValidUrl(url)) {
                loadMerchantLogoFromUrl(url)
            } else {
                decodeImageFromByteData(url)
            }
        }
    }

    private fun drawMerchantLogoFromName(name: String) {
        val staticLayout = getMerchangeNameLogoStaticLayout(name.substring(0, 1))

        val size = merchantLogoRadius * 2

        val bitmap: Bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.save()

        canvas.save()
        val logoBackground = ContextCompat.getDrawable(context, R.drawable.bg_monnify_logo_gradient)

        logoBackground!!.bounds = Rect(
            0, 0, size, size
        )
        logoBackground.draw(canvas)
        canvas.restore()

        canvas.save()

        val dx = size / 2 - staticLayout.width / 2
        val dy = size / 2 - staticLayout.height / 2

        canvas.translate(dx * 1f, dy * 1f)

        staticLayout.draw(canvas)

        canvas.restore()

        canvas.restore()

        setMerchantLogo(bitmap)
    }

    private fun decodeImageFromByteData(data: String) {

        try {
            val decodedString: ByteArray = Base64.decode(data, Base64.DEFAULT)

            val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

            setMerchantLogo(bitmap)
        } catch (e: Exception) {
        }
    }

    private fun loadMerchantLogoFromUrl(url: String?) {
        scope.launch {
            try {
                val data = loadMerchantLogoFromNetwork(url)
                setMerchantLogo(data)
            } catch (e: IOException) {
                Logger.log(this, e.message)
            }
        }
    }

    private suspend fun loadMerchantLogoFromNetwork(url: String?): Bitmap? {
        return withContext(Dispatchers.IO) {
            Picasso.get()
                .load(url)
                .transform(RoundedImageTransform(merchantLogoRadius, 0))
                .get()
        }
    }

    private fun setMerchantLogo(bitmap: Bitmap?) {
        merchantLogo = bitmap

        postInvalidate()
    }

    fun setMerchantName(value: String?) {
        merchantNameText = value ?: ""

        setupMerchantText(width)

        postInvalidate()
    }

    fun setTotalAmount(value: String?) {
        totalAmountText = value ?: ""

        setupTotalAmountText()

        postInvalidate()
    }

    fun setEmailAddress(value: String?) {
        emailAddressText = value ?: ""

        setupEmailText(width)

        postInvalidate()
    }

    fun setAmount(value: String?) {
        amountText = value ?: ""

        setupAmountText()

        postInvalidate()
    }

    fun setFee(value: String?) {
        feeText = value ?: ""

        setupFeeText()

        postInvalidate()
    }

}