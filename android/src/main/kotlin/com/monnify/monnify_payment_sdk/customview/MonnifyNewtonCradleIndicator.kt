package com.monnify.monnify_payment_sdk.customview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.google.android.material.math.MathUtils
import com.monnify.monnify_payment_sdk.R

class MonnifyNewtonCradleIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val dotRadius: Int
    private val dotColor: Int

    private val leftDotValueAnimator: ValueAnimator
    private val rightDotValueAnimator: ValueAnimator

    private val animatorSet: AnimatorSet

    init {

        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.MonnifyNewtonCradleIndicator)

        typedArray.apply {
            try {
                dotColor = getColor(
                    R.styleable.MonnifyNewtonCradleIndicator_cradleDotColor,
                    ContextCompat.getColor(context, R.color.monnifyDotIndicatorColor)
                )
                dotRadius = getDimensionPixelSize(
                    R.styleable.MonnifyNewtonCradleIndicator_cradleDotRadius,
                    context.resources.getDimensionPixelSize(R.dimen.dimen16dp)
                )
            } finally {
                recycle()
            }
        }

        leftDotValueAnimator = ValueAnimator.ofFloat(0f, 1f, 0f).apply {
            duration = 300
            interpolator = LinearInterpolator()
            addUpdateListener {
                leftTrans = it.animatedValue as Float
                invalidate()
            }
        }

        rightDotValueAnimator = ValueAnimator.ofFloat(0f, 1f, 0f).apply {
            duration = 300
            interpolator = LinearInterpolator()
            addUpdateListener {
                rightTrans = it.animatedValue as Float
                invalidate()
            }
        }

        animatorSet = AnimatorSet()
        animatorSet.playSequentially(leftDotValueAnimator, rightDotValueAnimator)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                animatorSet.start()
            }
        })
    }

    private var leftTrans: Float = 0f
    private var rightTrans: Float = 0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val dotDiameter: Int = dotRadius * 2

        val width: Int = dotDiameter * 8

        val height: Int =  dotDiameter * 4

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (canvas != null) {
            canvas.save()

            drawCenterDot(canvas)
            drawLeftDot(canvas)
            drawRightDot(canvas)

            canvas.restore()
        }
    }

    private fun drawRightDot(canvas: Canvas) {
        canvas.save()

        val x: Float = MathUtils.lerp(dotRadius * 2f, dotRadius * 3.5f, rightTrans)
        val y: Float = MathUtils.lerp(0f, dotRadius * 1f, rightTrans * rightTrans)

        canvas.translate(width / 2f + x, height / 2f - y)

        canvas.drawCircle(0f, 0f, dotRadius * 1f, Paint().apply {
            color = dotColor
            isAntiAlias = true
            style = Paint.Style.FILL

        })
        canvas.restore()
    }

    private fun drawLeftDot(canvas: Canvas) {
        canvas.save()

        val x: Float = MathUtils.lerp(dotRadius * 2f, dotRadius * 3.5f, leftTrans)
        val y: Float = MathUtils.lerp(0f, dotRadius * 1f, leftTrans * leftTrans)

        canvas.translate(width / 2f - x, height / 2f - y)

        canvas.drawCircle(0f, 0f, dotRadius * 1f, Paint().apply {
            color = dotColor
            isAntiAlias = true
            style = Paint.Style.FILL

        })

        canvas.restore()
    }

    private fun drawCenterDot(canvas: Canvas) {
        canvas.save()

        canvas.translate(width / 2f, height / 2f)

        canvas.drawCircle(0f, 0f, dotRadius * 1f, Paint().apply {
            color = dotColor
            isAntiAlias = true
            style = Paint.Style.FILL

        })

        canvas.restore()
    }

    fun start() {
        animatorSet.start()
    }

    fun end() {
        animatorSet.end()
    }
}