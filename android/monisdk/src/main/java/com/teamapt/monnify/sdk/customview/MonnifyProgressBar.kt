package com.teamapt.monnify.sdk.customview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.CountDownTimer
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import com.teamapt.monnify.sdk.R
import com.teamapt.monnify.sdk.util.Logger
import kotlin.math.roundToInt

@Suppress("DEPRECATION")
class MonnifyProgressBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_MAX_VALUE = 100.0f
    }

    enum class IndicatorType(val value: Int) {
        SMALL(value = 0),
        LARGE(value = 1);

        companion object {
            private val map = values()
                .associateBy(MonnifyProgressBar.IndicatorType::value)

            fun fromInt(value: Int) = map[value]
        }
    }

    private var periodInMillis: Long = 0
    private var timeLeftInTimeFormat: String = if (isInEditMode) "30 MINS 20 SECS" else ""

    var progress: Float = 0f
        set(value) {
            field = value.coerceIn(0f, DEFAULT_MAX_VALUE)
        }

    private var countDownTimer: CountDownTimer? = null


    private val indicatorType: IndicatorType

    private val indicatorHeight: Int

    private val indicatorPrefixText: String
    private val indicatorTextSize: Float
    private val indicatorTextColor: Int
    private val indicatorColor: Int
    private val indicatorTrackColor: Int
    private val indicatorTrackPattern: Int
    private val indicatorCornerRadius: Int
    private val showText: Boolean

    private lateinit var indicatorPrefixTextLayout: StaticLayout

    private lateinit var indicatorTimeRemainingTextLayout: StaticLayout


    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MonnifyProgressBar)

        typedArray.apply {

            try {

                val indicatorTypeValue: Int =
                    getInt(R.styleable.MonnifyProgressBar_indicatorType, IndicatorType.SMALL.value)

                indicatorType = IndicatorType.fromInt(indicatorTypeValue) ?: IndicatorType.SMALL

                indicatorHeight = getDimensionPixelSize(
                    R.styleable.MonnifyProgressBar_indicatorHeight,
                    context.resources.getDimensionPixelSize(R.dimen.dimen4dp)
                )

                indicatorPrefixText = getString(R.styleable.MonnifyProgressBar_indicatorText)
                    ?: if (indicatorType == IndicatorType.SMALL) context.resources.getString(R.string.account_expires_in) else context.resources.getString(
                        R.string.indicator_prefix
                    )

                indicatorColor = getColor(
                    R.styleable.MonnifyProgressBar_indicatorColor,
                    if (indicatorType == IndicatorType.SMALL) ContextCompat.getColor(
                        context,
                        R.color.monnifyRoyalBlue
                    ) else ContextCompat.getColor(context, R.color.monnifyIndicatorColor)
                )

                indicatorCornerRadius = getResourceId(
                    R.styleable.MonnifyProgressBar_indicatorCornerRadius,
                    if (indicatorType == IndicatorType.SMALL) context.resources.getDimensionPixelSize(
                        R.dimen.dimen50dp
                    ) else context.resources.getDimensionPixelSize(R.dimen.dimen16dp)
                )

                indicatorTrackColor = getColor(
                    R.styleable.MonnifyProgressBar_indicatorTrackColor,
                    if (indicatorType == IndicatorType.SMALL) ContextCompat.getColor(
                        context,
                        R.color.monnifySmallIndicatorTrackColor
                    ) else ContextCompat.getColor(context, R.color.monnifyIndicatorTrackColor)
                )

                indicatorTrackPattern = getResourceId(
                    R.styleable.MonnifyProgressBar_indicatorTrackPattern,
                    if (indicatorType == IndicatorType.SMALL) R.drawable.bg_small_progress_indicator_pattern else R.drawable.bg_progress_indicator_pattern
                )

                indicatorTextSize = getDimension(
                    R.styleable.MonnifyProgressBar_indicatorTextSize,
                    if (indicatorType == IndicatorType.SMALL) context.resources.getDimension(R.dimen.dimen10sp) else context.resources.getDimension(
                        R.dimen.dimen12sp
                    )
                )

                indicatorTextColor = getColor(
                    R.styleable.MonnifyProgressBar_indicatorTextColor,
                    if (indicatorType == IndicatorType.SMALL) ContextCompat.getColor(
                        context,
                        R.color.monnifyNeutralBlackTrans30
                    ) else ContextCompat.getColor(context, R.color.monnifySurfaceText)
                )

                showText = getBoolean(R.styleable.MonnifyProgressBar_indicatorShowText, true)

                setupProgressLabelText()
                setupProgressTimeRemainingText()

                if (isInEditMode) {
                    progress = 50.0f
                }

            } finally {
                recycle()
            }
        }
    }


    private var topPadding = 0
    private var bottomPadding = 0

    private var startPadding = 0
    private var endPadding = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        startPadding =
            if (ViewCompat.getPaddingStart(this) != 0) ViewCompat.getPaddingStart(this) else context.resources.getDimensionPixelSize(
                R.dimen.dimen10dp
            )

        endPadding =
            if (ViewCompat.getPaddingEnd(this) != 0) ViewCompat.getPaddingEnd(this) else context.resources.getDimensionPixelSize(
                R.dimen.dimen10dp
            )

        val width: Int = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            widthSize + ViewCompat.getPaddingEnd(this) + ViewCompat.getPaddingStart(this)
        }

        topPadding = when {
            paddingTop != 0 -> paddingTop
            indicatorType == IndicatorType.SMALL -> context.resources.getDimensionPixelSize(R.dimen.dimen4dp)
            else -> context.resources.getDimensionPixelSize(R.dimen.dimen8dp)
        }

        bottomPadding = when {
            paddingBottom != 0 -> paddingBottom
            indicatorType == IndicatorType.SMALL -> context.resources.getDimensionPixelSize(R.dimen.dimen4dp)
            else -> context.resources.getDimensionPixelSize(R.dimen.dimen8dp)
        }

        val height: Int = when (indicatorType) {
            IndicatorType.SMALL -> indicatorHeight + topPadding + bottomPadding + indicatorPrefixTextLayout.height
            else -> indicatorPrefixTextLayout.height + topPadding + bottomPadding
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (canvas != null) {
            canvas.save()

            if (indicatorType == IndicatorType.SMALL) {

                drawSmallTrackBackground(canvas)
                drawSmallProgressIndicator(canvas)
                drawSmallTrackPattern(canvas)
                if (showText) {
                    drawSmallIndicatorLabel(canvas)
                    drawSmallIndicatorTimeRemaining(canvas)
                }
            } else {
                clipLargeCanvas(canvas)
                drawLargeTrackBackground(canvas)
                drawLargeFilterOnBackground(canvas)
                drawLargeTrackPattern(canvas)
                drawLargeProgressIndicator(canvas)
                if (showText) {
                    drawLargeIndicatorLabel(canvas)
                    drawLargeIndicatorTimeRemaining(canvas)
                }
            }

            canvas.restore()
        }
    }

    private fun drawSmallTrackBackground(canvas: Canvas) {
        canvas.save()

        val radius = if(indicatorCornerRadius > height / 2) height / 4f else indicatorCornerRadius * 1f

        val path = Path().apply {
            moveTo(radius, 0f)
            lineTo(width - radius, 0f)
            arcTo(RectF(width - radius, 0f, width * 1f, radius), 270f, 90f)
            lineTo(width * 1f, indicatorHeight - radius)
            arcTo(RectF(width - radius, indicatorHeight - radius, width * 1f, indicatorHeight * 1f), 0f, 90f)
            lineTo(radius, indicatorHeight * 1f)
            arcTo(RectF(0f, indicatorHeight - radius, radius, indicatorHeight * 1f), 90f, 90f)
            lineTo(0f, radius)
            arcTo(RectF(0f, 0f, radius, radius), 180f, 90f)
            close()
        }

        val paint = Paint().apply {
            isAntiAlias = true
            color = indicatorTrackColor
            alpha = (0.25 * 255).toInt()
        }

        canvas.drawPath(path, paint)

        canvas.restore()
    }

    private fun drawSmallProgressIndicator(canvas: Canvas) {
        canvas.save()

        val dx = interpolate(progress, width * 0f, width * 1.0f)

        val radius = if(indicatorCornerRadius > height / 2) height / 4f else indicatorCornerRadius * 1f

        val path = Path().apply {
            moveTo(radius, 0f)
            lineTo(dx - radius, 0f)
            arcTo(RectF(dx - radius, 0f, dx, radius), 270f, 90f)
            lineTo(dx, indicatorHeight - radius)
            arcTo(RectF(dx - radius, indicatorHeight - radius, dx, indicatorHeight * 1f), 0f, 90f)
            lineTo(radius, indicatorHeight * 1f)
            arcTo(RectF(0f, indicatorHeight - radius, radius, indicatorHeight * 1f), 90f, 90f)
            lineTo(0f, radius)
            arcTo(RectF(0f, 0f, radius, radius), 180f, 90f)
            close()
        }

        val paint = Paint().apply {
            isAntiAlias = true
            color = indicatorColor
            style = Paint.Style.FILL_AND_STROKE
            alpha = (0.5 * 255).toInt()
        }

        canvas.drawPath(path, paint)

        canvas.restore()
    }

    private fun drawSmallTrackPattern(canvas: Canvas) {
        canvas.save()

        val dx = interpolate(progress, width * 0f, width * 1.0f)

        val bitmap: Bitmap = ContextCompat.getDrawable(context, indicatorTrackPattern)!!.toBitmap()

        val bitmapDrawable = BitmapDrawable(context.resources, bitmap)

        bitmapDrawable.tileModeX = Shader.TileMode.REPEAT
        bitmapDrawable.setBounds(0, 0, dx.toInt(), indicatorHeight)
        bitmapDrawable.alpha = (0.25 * 255).toInt()

        bitmapDrawable.draw(canvas)

        canvas.restore()
    }

    private fun drawSmallIndicatorLabel(canvas: Canvas) {
        canvas.save()

        val dx = (width - indicatorPrefixTextLayout.width - indicatorTimeRemainingTextLayout.width) / 2
        val dy = indicatorHeight + topPadding

        canvas.translate(dx * 1f, dy * 1f)

        indicatorPrefixTextLayout.draw(canvas)

        canvas.restore()
    }

    private fun drawSmallIndicatorTimeRemaining(canvas: Canvas) {
        canvas.save()

        val dx = (width - indicatorPrefixTextLayout.width - indicatorTimeRemainingTextLayout.width) / 2 + indicatorPrefixTextLayout.width
        val dy = indicatorHeight + topPadding

        canvas.translate(dx * 1f, dy * 1f)

        indicatorTimeRemainingTextLayout.draw(canvas)

        canvas.restore()
    }

    private fun clipLargeCanvas(canvas: Canvas) {

        val radius = indicatorCornerRadius.toFloat()

        val path = Path().apply {
            moveTo(radius, 0f)
            lineTo(width - radius, 0f)
            arcTo(RectF(width - radius, 0f, width * 1f, radius), 270f, 90f)
            lineTo(width * 1f, height - radius)
            arcTo(RectF(width - radius, height - radius, width * 1f, height * 1f), 0f, 90f)
            lineTo(radius, height * 1f)
            arcTo(RectF(0f, height - radius, radius, height * 1f), 90f, 90f)
            lineTo(0f, radius)
            arcTo(RectF(0f, 0f, radius, radius), 180f, 90f)
            close()
        }

        canvas.clipPath(path)
    }

    private fun drawLargeProgressIndicator(canvas: Canvas) {
        canvas.save()

        val dx = interpolate(progress, width * 0f, width * 1.0f)

        val radius = indicatorCornerRadius.toFloat()

        val path = Path().apply {
            moveTo(radius, 0f)
            lineTo(dx - radius, 0f)
            arcTo(RectF(dx - radius, 0f, dx, radius), 270f, 90f)
            lineTo(dx, height - radius)
            arcTo(RectF(dx - radius, height - radius, dx, height * 1f), 0f, 90f)
            lineTo(radius, height * 1f)
            arcTo(RectF(0f, height - radius, radius, height * 1f), 90f, 90f)
            lineTo(0f, radius)
            arcTo(RectF(0f, 0f, radius, radius), 180f, 90f)
            close()
        }

        val paint = Paint().apply {
            color = indicatorColor
            style = Paint.Style.FILL_AND_STROKE
        }

        canvas.drawPath(path, paint)

        canvas.restore()
    }

    private fun drawLargeIndicatorLabel(canvas: Canvas) {
        canvas.save()

        val dx = bottomPadding + indicatorPrefixTextLayout.height
        val dy = startPadding

        canvas.translate(dx * 1.0f, dy * 1.0f)

        indicatorPrefixTextLayout.draw(canvas)

        canvas.restore()
    }

    private fun drawLargeIndicatorTimeRemaining(canvas: Canvas) {
        canvas.save()

        val dx = bottomPadding + indicatorPrefixTextLayout.height + indicatorPrefixTextLayout.width
        val dy = startPadding

        canvas.translate(dx * 1.0f, dy * 1.0f)

        indicatorTimeRemainingTextLayout.draw(canvas)

        canvas.restore()
    }

    private fun drawLargeTrackPattern(canvas: Canvas) {
        canvas.save()

        val bitmap: Bitmap = ContextCompat.getDrawable(context, indicatorTrackPattern)!!.toBitmap()

        val bitmapDrawable = BitmapDrawable(context.resources, bitmap)

        bitmapDrawable.tileModeX = Shader.TileMode.REPEAT
        bitmapDrawable.setBounds(0, 0, width, height)
        bitmapDrawable.alpha = (0.25 * 255).toInt()

        bitmapDrawable.draw(canvas)

        canvas.restore()
    }

    private fun drawLargeFilterOnBackground(canvas: Canvas) {
        canvas.save()

        val colorDrawable = ColorDrawable()
        colorDrawable.color = Color.parseColor("#4DB4CBD5")

        colorDrawable.setBounds(0, 0, width, height)

        colorDrawable.draw(canvas)

        canvas.restore()
    }

    private fun drawLargeTrackBackground(canvas: Canvas) {
        canvas.save()

        val colorDrawable = ColorDrawable(indicatorTrackColor)

        colorDrawable.setBounds(0, 0, width, height)

        colorDrawable.draw(canvas)

        canvas.restore()
    }

    private fun setupProgressLabelText() {


        val textPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = indicatorTextSize
            color = indicatorTextColor
            isFakeBoldText = indicatorType != IndicatorType.SMALL
            typeface =
                when {
                    isInEditMode -> null
                    indicatorType == IndicatorType.SMALL -> ResourcesCompat.getFont(
                        context,
                        R.font.inter_regular
                    )
                    else -> ResourcesCompat.getFont(context, R.font.rubik_bold)
                }

            if (indicatorType != IndicatorType.SMALL) {
                setShadowLayer(
                    1f,
                    0f,
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX,
                        4f,
                        context.resources.displayMetrics
                    ),
                    ContextCompat.getColor(context, R.color.monnifyInputHintColor)
                )
            }
        }

        val text = "$indicatorPrefixText "

        val textWidth: Int = textPaint.measureText(text).roundToInt()


        indicatorPrefixTextLayout =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(
                    text,
                    0,
                    text.length,
                    textPaint,
                    textWidth
                ).setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setIncludePad(false)
                    .setMaxLines(1)
                    .setLineSpacing(0f, 14.65f)
                    .build()
            } else {
                StaticLayout(
                    text,
                    0,
                    text.length,
                    textPaint,
                    textWidth,
                    Layout.Alignment.ALIGN_NORMAL,
                    14.65f,
                    0f,
                    false
                )
            }
    }

    private fun setupProgressTimeRemainingText() {

        val textPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = indicatorTextSize
            color = indicatorTextColor
            isFakeBoldText = true
            typeface =
                when {
                    isInEditMode -> null
                    indicatorType == IndicatorType.SMALL -> ResourcesCompat.getFont(
                        context,
                        R.font.inter_bold
                    )
                    else -> ResourcesCompat.getFont(context, R.font.rubik_bold)
                }

            if (indicatorType != IndicatorType.SMALL) {
                setShadowLayer(
                    1f,
                    0f,
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX,
                        4f,
                        context.resources.displayMetrics
                    ),
                    ContextCompat.getColor(context, R.color.monnifyInputHintColor)
                )
            }
        }

        val text = timeLeftInTimeFormat

        val textWidth: Int = textPaint.measureText(text).roundToInt()


        indicatorTimeRemainingTextLayout =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(
                    text,
                    0,
                    text.length,
                    textPaint,
                    textWidth
                ).setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setIncludePad(false)
                    .setMaxLines(1)
                    .setLineSpacing(0f, 14.65f)
                    .build()
            } else {
                StaticLayout(
                    text,
                    0,
                    text.length,
                    textPaint,
                    textWidth,
                    Layout.Alignment.ALIGN_NORMAL,
                    14.65f,
                    0f,
                    false
                )
            }
    }

    private fun interpolate(value: Float, y1: Float, y2: Float): Float {
        return y1 + ((value) * (y2 - y1) / (DEFAULT_MAX_VALUE))
    }

    fun startCountDown(
        periodInMillis: Long,
        countDownCompleteListener: () -> Unit
    ) {

        this.periodInMillis = periodInMillis

        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(periodInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                Logger.log(this, "v: $millisUntilFinished")

                setTimeLeftInTimeFormat(millisUntilFinished)

                setupProgressTimeRemainingText()

                val currentProgress =
                    (DEFAULT_MAX_VALUE - ((millisUntilFinished * DEFAULT_MAX_VALUE / periodInMillis)))

                progress = currentProgress

                invalidate()
            }

            override fun onFinish() {
                if (periodInMillis != 0L)
                    onTick(0L)
                countDownCompleteListener.invoke()
            }
        }

        (countDownTimer as CountDownTimer).start()

    }

    private fun setTimeLeftInTimeFormat(millisUntilFinished: Long) {
        val remainingMin = millisUntilFinished / 60000
        val secondsModulus = String.format("%02d", (millisUntilFinished % 60000) / 1000)
        this.timeLeftInTimeFormat = if(indicatorType == IndicatorType.SMALL) "$remainingMin min $secondsModulus sec" else "$remainingMin MIN $secondsModulus SECS"
    }

    override fun onDetachedFromWindow() {
        countDownTimer?.cancel()
        super.onDetachedFromWindow()
    }

}