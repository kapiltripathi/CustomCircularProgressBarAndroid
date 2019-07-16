package com.example.customcircularprogressbarandroid

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Color.rgb
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator

class CustomProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mViewWidth: Int = 0
    private var mViewHeight: Int = 0

    private val mStartAngle = -90f      // Always start from top (default is: "3 o'clock on a watch.")
    private var mSweepAngle = 0f              // How long to sweep from mStartAngle
    private val mMaxSweepAngle = 360f         // Max degrees to sweep = full circle
    private var mStrokeWidth: Int // Width of outline
    private var mAnimationDuration: Int       // Animation duration for progress change
    private var mMaxProgress: Int            // Max progress to use
    private var mDrawText = true           // Set to true if progress text should be drawn
    private var mRoundedCorners = true     // Set to true if rounded corners should be applied to outline ends
    private var mProgressColor: Int       // Outline color
    private var mTextColor = Color.BLACK       // Progress text color

    private val mPaint: Paint                 // Allocate paint outside onDraw to avoid unnecessary object creation

    init {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }
    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomProgressBar,
            defStyleAttr, 0).apply {

            try {
                mStrokeWidth =getDimensionPixelSize(R.styleable.CustomProgressBar_mStrokeWidth,10)
                mAnimationDuration=getInteger(R.styleable.CustomProgressBar_mAnimationDuration,100)
                mMaxProgress = getInteger(R.styleable.CustomProgressBar_mMaxProgress,100)
                mProgressColor = getColor(R.styleable.CustomProgressBar_mProgressColor,rgb(250,0,0))


                //textPos = getInteger(R.styleable.PieChart_labelPosition, 0)
            } finally {
                recycle()
            }
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        initMeasurements()
        drawOutlineArc(canvas)

        if (mDrawText) {
            drawText(canvas)
        }
    }

    private fun initMeasurements() {
        mViewWidth = width
        mViewHeight = height
    }

    private fun drawOutlineArc(canvas: Canvas) {

        val diameter1 = (Math.min(mViewWidth, mViewHeight) - mStrokeWidth * 2)+1

        val outerOval1 = RectF(mStrokeWidth.toFloat(), mStrokeWidth.toFloat(), diameter1.toFloat(), diameter1.toFloat())

        mPaint.color = rgb(238,238,238)//mProgressColor
        mPaint.strokeWidth = mStrokeWidth.toFloat()
        mPaint.isAntiAlias = true
        //rounding corners is done here

        mPaint.strokeCap = if (mRoundedCorners) Paint.Cap.ROUND else Paint.Cap.BUTT
        mPaint.style = Paint.Style.STROKE
        canvas.drawArc(outerOval1, -90f+mSweepAngle, 360f-mSweepAngle, false, mPaint)




        val diameter = Math.min(mViewWidth, mViewHeight) - mStrokeWidth * 2

        val outerOval = RectF(mStrokeWidth.toFloat(), mStrokeWidth.toFloat(), diameter.toFloat(), diameter.toFloat())

        mPaint.color = mProgressColor
        mPaint.strokeWidth = mStrokeWidth.toFloat()
        mPaint.isAntiAlias = true
        mPaint.strokeCap = if (mRoundedCorners) Paint.Cap.ROUND else Paint.Cap.BUTT
        mPaint.style = Paint.Style.STROKE
        canvas.drawArc(outerOval, mStartAngle, mSweepAngle, false, mPaint)
    }

    private fun drawText(canvas: Canvas) {
        mPaint.textSize = Math.min(mViewWidth, mViewHeight) / 5f
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.strokeWidth = 0f
        mPaint.color = mTextColor

        // Center text
        val xPos =   canvas.width / 2
        val yPos =   (canvas.height / 2 - (mPaint.descent() + mPaint.ascent()) / 2).toInt()

        canvas.drawText(
            calcProgressFromSweepAngle(mSweepAngle).toString(),
            xPos.toFloat(),
            yPos.toFloat(),
            mPaint
        )
    }

    private fun calcSweepAngleFromProgress(progress: Float): Float {
        return mMaxSweepAngle / mMaxProgress * progress
    }

    private fun calcProgressFromSweepAngle(sweepAngle: Float): Float {
        return (sweepAngle * mMaxProgress / mMaxSweepAngle)
    }

    /**
     * Set progress of the circular progress bar.
     * @param progress progress between 0 and 100.
     */
    fun setProgress(progress: Float) {
        val animator = ValueAnimator.ofFloat(mSweepAngle, calcSweepAngleFromProgress(progress))
        animator.interpolator = DecelerateInterpolator()
        animator.duration = mAnimationDuration.toLong()
        animator.addUpdateListener { valueAnimator ->
            mSweepAngle = valueAnimator.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    fun setProgressColor(color: Int) {
        mProgressColor = color
        invalidate()
    }

    fun setProgressWidth(width: Int) {
        mStrokeWidth = width
        invalidate()
    }

    fun setTextColor(color: Int) {
        mTextColor = color
        invalidate()
    }

    fun showProgressText(show: Boolean) {
        mDrawText = show
        invalidate()
    }

    /**
     * Toggle this if you don't want rounded corners on progress bar.
     * Default is true.
     * @param roundedCorners true if you want rounded corners of false otherwise.
     */
    fun useRoundedCorners(roundedCorners: Boolean) {
        mRoundedCorners = roundedCorners
        invalidate()
    }
}