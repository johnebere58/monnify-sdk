package com.teamapt.monnify.sdk.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.core.content.ContextCompat
import com.monnify.monnify_payment_sdk.R

class MonnifyProcessingIndicatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private val indicatorStrokeWidth: Int
    private val indicatorStrokeColor: Int
    private val indicatorSize: Int

    private var rotateAnimation: RotateAnimation


    init {
        val typeArray = context.obtainStyledAttributes(R.styleable.MonnifyProcessingIndicatorView)

        typeArray.apply {
            try {
                indicatorSize = getDimensionPixelSize(
                    R.styleable.MonnifyProcessingIndicatorView_processingIndicatorSize,
                    context.resources.getDimensionPixelSize(R.dimen.dimen24dp)
                )

                indicatorStrokeWidth = getDimensionPixelSize(
                    R.styleable.MonnifyProcessingIndicatorView_processingIndicatorStrokeWidth,
                    context.resources.getDimensionPixelSize(R.dimen.dimen2dp)
                )

                indicatorStrokeColor = getColor(
                    R.styleable.MonnifyProcessingIndicatorView_processingIndicatorStrokeColor,
                    if (!isInEditMode) ContextCompat.getColor(
                        context,
                        R.color.monnifyWhite
                    ) else ContextCompat.getColor(context, R.color.monnifyRoyalGold)
                )



                rotateAnimation = RotateAnimation(
                    0.0f,
                    360.0f,
                    RotateAnimation.RELATIVE_TO_SELF,
                    0.5f,
                    RotateAnimation.RELATIVE_TO_SELF,
                    0.5f
                ).apply {
                    interpolator = LinearInterpolator()
                    duration = 2500L
                    repeatCount = Animation.INFINITE
                }


            } finally {
                recycle()
            }
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val size = indicatorSize

        setMeasuredDimension(size, size)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (canvas != null) {
            canvas.save()

            drawIndicator(canvas)

            canvas.translate(width / 2f, height / 2f)

            canvas.restore()
        }
    }

    private fun drawIndicator(canvas: Canvas) {
        canvas.save()

        val angle = 360 / 12f

        canvas.translate(width / 2f, height / 2f)

        val paint = Paint().apply {
            color = indicatorStrokeColor
            style = Paint.Style.STROKE
            strokeWidth = indicatorStrokeWidth * 1f
            strokeCap = Paint.Cap.ROUND
        }

        for (x: Int in 0 until 12) {

            val path = Path().apply {

                val xHeight = width / 2f

                moveTo(0f, xHeight * 0.65f)
                lineTo(0f, xHeight * 0.90f)
            }

            canvas.drawPath(path, paint)

            canvas.rotate(angle)
        }

        canvas.restore()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        clearAnimation()
    }

    fun start() {
        startAnimation(rotateAnimation)
    }

    fun end() {
        clearAnimation()
    }

}