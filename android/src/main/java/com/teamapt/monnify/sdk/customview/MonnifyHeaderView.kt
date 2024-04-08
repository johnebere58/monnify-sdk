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
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.contains
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import com.squareup.picasso.Picasso
import com.monnify.monnify_payment_sdk.R
import com.teamapt.monnify.sdk.data.RoundedImageTransform
import com.teamapt.monnify.sdk.util.Logger
import kotlinx.coroutines.*
import java.io.IOException
import kotlin.math.*

@Suppress("DEPRECATION")
class MonnifyHeaderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val topCornerRadius: Int
    private val gapSize: Int

    private val background: Int
    private val overlay: Int

    private var labelText: String
    private val labelTextSize: Int
    private val labelTextColor: Int

    private var amountText: String
    private val amountTextSize: Int
    private val amountTextColor: Int

    private val notchColor: Int
    private val notchWidth: Int
    private val notchHeight: Int

    private var actionBarUsernameText: String
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

    private val merchantLogoRadius: Int
    private val merchantLogoBorderWidth: Int
    private val merchantLogoBackground: Int
    private val merchantLogoBorderColor: Int

    private var merchantNameText: String
    private val merchantNameTextSize: Int
    private val merchantNameTextColor: Int

    private lateinit var actionBarUsernameLayout: StaticLayout
    private lateinit var actionBarCancelTextLayout: StaticLayout
    private lateinit var labelTextLayout: StaticLayout
    private lateinit var amountTextLayout: StaticLayout
    private lateinit var merchantNameLayout: StaticLayout

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
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.MonnifyHeaderView)

        typedArray.apply {

            try {
                topCornerRadius = getDimensionPixelSize(
                    R.styleable.MonnifyHeaderView_headerViewCornerRadius,
                    context.resources.getDimensionPixelSize(R.dimen.dimen16dp)
                )
                gapSize = getDimensionPixelOffset(
                    R.styleable.MonnifyHeaderView_headerViewGapSize,
                    context.resources.getDimensionPixelSize(R.dimen.dimen4dp)
                )

                background = getResourceId(
                    R.styleable.MonnifyHeaderView_headerViewBackground,
                    R.drawable.bg_amount_gradient
                )

                overlay = getResourceId(
                    R.styleable.MonnifyHeaderView_headerViewOverlay,
                    R.drawable.bg_amount_pattern
                )

                notchColor = getColor(
                    R.styleable.MonnifyHeaderView_headerViewNotchColor,
                    ContextCompat.getColor(context, R.color.monnifyNotchColor)
                )

                notchWidth = getDimensionPixelSize(
                    R.styleable.MonnifyHeaderView_headerViewNotchWidth,
                    context.resources.getDimensionPixelOffset(R.dimen.notchWidth)
                )

                notchHeight = (notchWidth * 0.25).toInt()

                labelText = getString(
                    R.styleable.MonnifyHeaderView_headerViewLabelText
                ) ?: context.resources.getString(R.string.amount_to_be_paid)

                labelTextSize = getDimensionPixelSize(
                    R.styleable.MonnifyHeaderView_headerViewLabelTextSize,
                    context.resources.getDimensionPixelSize(R.dimen.dimen12sp)
                )
                labelTextColor =
                    getColor(
                        R.styleable.MonnifyHeaderView_headerViewLabelTextColor,
                        ContextCompat.getColor(context, R.color.monnifyAmountLabel)
                    )

                amountText = getString(
                    R.styleable.MonnifyHeaderView_headerViewAmountText
                ) ?: context.resources.getString(R.string.default_amount)

                amountTextSize = getDimensionPixelSize(
                    R.styleable.MonnifyHeaderView_headerViewAmountTextSize,
                    context.resources.getDimensionPixelSize(R.dimen.dimen32sp)
                )
                amountTextColor =
                    getColor(
                        R.styleable.MonnifyHeaderView_headerViewAmountTextColor,
                        ContextCompat.getColor(context, R.color.monnifySurfaceText)
                    )

                actionBarUsernameText =
                    getString(R.styleable.MonnifyHeaderView_headerViewActionBarUsernameText)
                        ?: if (isInEditMode) context.resources.getString(R.string.hint_customer_name) else ""

                actionBarCancelText =
                    getString(R.styleable.MonnifyHeaderView_headerViewActionBarCancelText)
                        ?: context.resources.getString(R.string.cancel)

                actionBarCancelIcon = getResourceId(
                    R.styleable.MonnifyHeaderView_headerViewActionBarCancelIcon,
                    R.drawable.ic_close
                )

                actionBarCancelIconSize = getDimensionPixelSize(
                    R.styleable.MonnifyHeaderView_headerViewActionBarCancelIconSize,
                    context.resources.getDimensionPixelSize(R.dimen.dimen12dp)
                )

                actionBarTextSize = getDimensionPixelSize(
                    R.styleable.MonnifyHeaderView_headerViewActionBarTextSize,
                    context.resources.getDimensionPixelSize(R.dimen.dimen12sp)
                )

                actionBarTextColor = getColor(
                    R.styleable.MonnifyHeaderView_headerViewActionBarTextColor,
                    ContextCompat.getColor(context, R.color.monnifySurfaceText)
                )

                actionBarTopPadding = getDimensionPixelSize(
                    R.styleable.MonnifyHeaderView_headerViewActionBarTopPadding,
                    context.resources.getDimensionPixelSize(R.dimen.dimen27dp)
                )

                actionBarBottomPadding = getDimensionPixelSize(
                    R.styleable.MonnifyHeaderView_headerViewActionBarBottomPadding,
                    context.resources.getDimensionPixelSize(R.dimen.dimen5dp)
                )

                actionBarStartPadding = getDimensionPixelSize(
                    R.styleable.MonnifyHeaderView_headerViewActionBarStartPadding,
                    context.resources.getDimensionPixelSize(R.dimen.dimen18dp)
                )

                actionBarEndPadding = getDimensionPixelSize(
                    R.styleable.MonnifyHeaderView_headerViewActionBarEndPadding,
                    context.resources.getDimensionPixelSize(R.dimen.dimen18dp)
                )

                merchantLogoRadius = getDimensionPixelSize(
                    R.styleable.MonnifyHeaderView_headerViewMerchantLogoRadius,
                    context.resources.getDimensionPixelOffset(R.dimen.merchantLogoRadius)
                )

                merchantLogoBorderWidth = getDimensionPixelSize(
                    R.styleable.MonnifyHeaderView_headerViewMerchantLogoBorderWidth,
                    context.resources.getDimensionPixelOffset(R.dimen.dimen4dp)
                )

                merchantLogoBorderColor = getColor(
                    R.styleable.MonnifyHeaderView_headerViewMerchantLogoBorderColor,
                    ContextCompat.getColor(context, R.color.monnifyWhite)
                )

                merchantLogoBackground = getResourceId(
                    R.styleable.MonnifyHeaderView_headerViewMerchantLogoBackground,
                    R.drawable.bg_merchant_logo_gradient
                )

                merchantNameText =
                    getString(R.styleable.MonnifyHeaderView_headerViewMerchantNameText)
                        ?: if (isInEditMode) context.resources.getString(R.string.hint_merchant_name) else ""

                merchantNameTextSize = getDimensionPixelSize(
                    R.styleable.MonnifyHeaderView_headerViewMerchantNameTextSize,
                    context.resources.getDimensionPixelOffset(R.dimen.dimen12dp)
                )

                merchantNameTextColor = getColor(
                    R.styleable.MonnifyHeaderView_headerViewMerchantNameTextColor,
                    ContextCompat.getColor(context, R.color.monnifyWhite)
                )

            } finally {
                recycle()
            }
        }
    }

    private var topPadding = 0
    private var bottomPadding = 0

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

        setupUsernameText(width = width)
        setupCancelText()
        setupLabelText()
        setupAmountText()
        setupMerchantText()

        contentHeight = labelTextLayout.height + gapSize + amountTextLayout.height

        actionBarHeight =
            actionBarUsernameLayout.height + actionBarTopPadding + actionBarBottomPadding

        topPadding =
            if (paddingTop != 0) paddingTop else context.resources.getDimensionPixelSize(R.dimen.dimen60dp)

        bottomPadding =
            if (paddingBottom != 0) paddingBottom else context.resources.getDimensionPixelSize(R.dimen.dimen12dp)

        val height: Int = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            if (heightMode == MeasureSpec.AT_MOST)
                heightSize.coerceAtMost(contentHeight + topPadding + bottomPadding + actionBarHeight)
            else
                contentHeight + actionBarHeight
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        if (canvas != null) {
            canvas.save()

            drawUsernameText(canvas)
            drawCancelText(canvas)
            drawCurveCorner(canvas)
            drawBackground(canvas)
            drawMerchantNotch(canvas)
            drawMerchantLogo(canvas)
            drawMerchantName(canvas)
            drawLabelText(canvas)
            drawAmountText(canvas)
            drawOverlayPattern(canvas)

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

    private fun setupUsernameText(width: Int) {
        val usernameTextPaint: TextPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = actionBarTextSize * 1f
            color = actionBarTextColor
            typeface =
                if (isInEditMode) null else ResourcesCompat.getFont(context, R.font.oxygen_bold)
            isFakeBoldText = true
        }

        val textWidth: Float = usernameTextPaint.measureText(actionBarUsernameText)

        val ellipsizeWidth: Int = (width * 0.45).toInt()


        actionBarUsernameLayout =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(
                    actionBarUsernameText,
                    0,
                    actionBarUsernameText.length,
                    usernameTextPaint,
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
                    actionBarUsernameText,
                    0,
                    actionBarUsernameText.length,
                    usernameTextPaint,
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

    private fun setupMerchantText() {
        val merchantNameTextPaint: TextPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = merchantNameTextSize * 1f
            color = merchantNameTextColor
            typeface =
                if (isInEditMode) null else ResourcesCompat.getFont(context, R.font.rubik_bold)
            isFakeBoldText = true
        }

        val textWidth: Float = notchWidth * 0.70f

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
                    setAlignment(Layout.Alignment.ALIGN_CENTER)
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
                    Layout.Alignment.ALIGN_CENTER,
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

        actionBarCancelTextLayout =
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

    private fun setupLabelText() {
        val labelTextPaint: TextPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = labelTextSize * 1f
            color = labelTextColor
            typeface =
                if (isInEditMode) null else ResourcesCompat.getFont(context, R.font.rubik_regular)
        }

        val textWidth: Float = labelTextPaint.measureText(labelText)

        labelTextLayout =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(
                    labelText,
                    0,
                    labelText.length,
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
                    labelText,
                    0,
                    labelText.length,
                    labelTextPaint,
                    textWidth.roundToInt(),
                    Layout.Alignment.ALIGN_CENTER,
                    1f,
                    0f,
                    false
                )
            }
    }

    private fun setupAmountText() {
        val amountTextPaint: TextPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = amountTextSize * 1f
            color = amountTextColor
            typeface =
                if (isInEditMode) null else ResourcesCompat.getFont(context, R.font.rubik_bold)
            isFakeBoldText = true
        }

        val textWidth: Float = amountTextPaint.measureText(labelText)

        amountTextLayout =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(
                    amountText,
                    0,
                    amountText.length,
                    amountTextPaint,
                    textWidth.roundToInt()
                ).apply {
                    setAlignment(Layout.Alignment.ALIGN_CENTER)
                    setIncludePad(false)
                    setMaxLines(1)
                    setLineSpacing(0f, 1f)
                }.build()
            } else {
                StaticLayout(
                    amountText,
                    0,
                    amountText.length,
                    amountTextPaint,
                    textWidth.roundToInt(),
                    Layout.Alignment.ALIGN_CENTER,
                    1f,
                    0f,
                    false
                )
            }
    }

    private fun drawUsernameText(canvas: Canvas) {
        canvas.save()

        val dx: Float = actionBarStartPadding * 1f
        val dy: Float = actionBarTopPadding * 1f

        canvas.translate(dx, dy)
        actionBarUsernameLayout.draw(canvas)

        canvas.restore()
    }

    private fun drawCancelText(canvas: Canvas) {
        canvas.save()

        val dx: Float =
            width - actionBarEndPadding - actionBarCancelIconSize - actionBarCancelIconSize * 0.25f - actionBarCancelTextLayout.width * 1f
        val dy: Float = actionBarTopPadding * 1f

        canvas.translate(dx, dy)
        actionBarCancelTextLayout.draw(canvas)

        val bitmap = ContextCompat.getDrawable(context, actionBarCancelIcon)!!.toBitmap()
        val bitmapDrawable = BitmapDrawable(context.resources, bitmap)

        val left: Int =
            (actionBarCancelTextLayout.width + actionBarCancelIconSize * 0.25).toInt()
        val top: Int = (actionBarCancelTextLayout.height * 0.10).toInt()


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

    private fun drawLabelText(canvas: Canvas) {

        canvas.save()

        val dx: Float = ((width / 2f) - labelTextLayout.width / 2f)
        val dy: Float =
            height - (bottomPadding + 0f) - amountTextLayout.height - labelTextLayout.height

        canvas.translate(dx, dy)
        labelTextLayout.draw(canvas)

        canvas.restore()
    }

    private fun drawAmountText(canvas: Canvas) {
        canvas.save()

        val dx: Float = ((width / 2f) - amountTextLayout.width / 2f)
        val dy: Float = height - (bottomPadding + 0f) - amountTextLayout.height

        canvas.translate(dx, dy)
        amountTextLayout.draw(canvas)

        canvas.restore()
    }

    private fun drawOverlayPattern(canvas: Canvas) {
        canvas.save()

        val bitmap: Bitmap =
            ContextCompat.getDrawable(context, overlay)!!.toBitmap()
        val bitmapDrawable = BitmapDrawable(context.resources, bitmap)

        bitmapDrawable.setBounds(0, 0, width, height)
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)

        bitmapDrawable.alpha = 25

        canvas.translate(0f, actionBarHeight * 1f)

        bitmapDrawable.draw(canvas)

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

            lineTo(width / 2f - merchantLogoRadius - merchantLogoBorderWidth, actionBarHeight * 1f)

            arcTo(
                RectF(
                    width / 2f - merchantLogoRadius - merchantLogoBorderWidth * 1.5f,
                    actionBarHeight * 1f - merchantLogoRadius - merchantLogoBorderWidth * 1.5f,
                    width / 2f + merchantLogoRadius + merchantLogoBorderWidth * 1.5f,
                    actionBarHeight * 1f + merchantLogoRadius + merchantLogoBorderWidth * 1.5f,
                ),
                180f, 180f
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

    private fun drawBackground(canvas: Canvas) {

        val drawable: Drawable = ContextCompat.getDrawable(context, background)!!
        drawable.setBounds(0, actionBarHeight, width, height)
//        drawable.alpha = 126

        drawable.draw(canvas)
    }

    private fun drawMerchantNotch(canvas: Canvas) {
        canvas.save()

        val itemWidth = notchWidth * 1f
        val itemHeight = notchHeight * 1f

        val degreeToRadian: Float = (PI / 180).toFloat()

        val path: Path = Path().apply {
            moveTo(0f, 0f)
            lineTo(itemWidth, 0f)
            lineTo(itemWidth * 0.90f, itemHeight * 0.75f)

            val rWidth: Float = tan(30 * degreeToRadian) * itemHeight

            cubicTo(
                itemWidth - rWidth,
                itemHeight * 1.0f,
                itemWidth - rWidth,
                itemHeight * 1.0f,
                itemWidth * 0.70f,
                itemHeight * 1.0f
            )

            lineTo(itemWidth * 0.30f, itemHeight)

            cubicTo(
                rWidth,
                itemHeight * 1.0f,
                rWidth,
                itemHeight * 1.0f,
                itemWidth * 0.10f,
                itemHeight * 0.75f
            )

            lineTo(0f, 0f)
        }

        val paint: Paint = Paint().apply {
            style = Paint.Style.FILL
            color = notchColor
        }

        canvas.translate(width / 2f - itemWidth / 2f, actionBarHeight * 1f)

        canvas.drawPath(path, paint)

        canvas.restore()
    }


    private fun drawMerchantLogo(canvas: Canvas) {
        canvas.save()

        val cx: Float = width / 2f
        val cy: Float = actionBarHeight * 1f

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

        val dx: Float = width / 2f - notchWidth * 0.35f
        val dy: Float = actionBarHeight * 1f + merchantLogoRadius + merchantLogoBorderWidth * 1.5f

        canvas.translate(dx, dy)

        merchantNameLayout.draw(canvas)

        canvas.restore()
    }

    fun loadMerchantLogo(url: String?) {
        scope.launch {
            try {
                merchantLogo = loadMerchantLogoInBackground(url)

                postInvalidate()
            } catch (e: IOException) {
                Logger.log(this, e.message)
            }
        }
    }

    private suspend fun loadMerchantLogoInBackground(url: String?): Bitmap? {
        return withContext(Dispatchers.IO) {
            Picasso.get()
                .load(url)
                .transform(RoundedImageTransform(merchantLogoRadius, 0))
                .get()
        }
    }

    fun setMerchantLogo(bitmap: Bitmap?) {
        merchantLogo = bitmap

        postInvalidate()
    }

    fun setMerchantName(value: String?) {
        merchantNameText = value ?: ""

        setupMerchantText()

        postInvalidate()
    }

    fun setAmount(value: String?) {
        amountText = value ?: ""

        setupAmountText()

        postInvalidate()
    }

    fun setUsername(value: String?) {
        actionBarUsernameText = value ?: ""

        setupUsernameText(width)

        postInvalidate()
    }
}