package app.launch0.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import app.launch0.R

class AlphabetIndexView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val letters = listOf("#") + ('A'..'Z').map { it.toString() }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
        textSize = sp(10f)
    }

    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("sans-serif", Typeface.BOLD)
        textSize = sp(10f)
    }

    private var activeLetter: String? = null
    private var letterAnimProgress = 0f
    private var letterAnimator: ValueAnimator? = null
    private var isTouching = false

    var onLetterSelected: ((String) -> Unit)? = null
    var onLetterDeselected: (() -> Unit)? = null
    var appLabelGravity: Int = Gravity.START
        set(value) {
            field = value
            invalidate()
        }

    private var normalColor: Int = 0
    private var dimColor: Int = 0
    private var highlightColor: Int = 0

    init {
        resolveColors()
    }

    private fun resolveColors() {
        val typedValue = TypedValue()

        context.theme.resolveAttribute(R.attr.primaryColor, typedValue, true)
        normalColor = typedValue.data
        highlightColor = normalColor

        context.theme.resolveAttribute(R.attr.primaryColorTrans50, typedValue, true)
        dimColor = typedValue.data

        textPaint.color = dimColor
        highlightPaint.color = highlightColor
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (letters.isEmpty()) return

        val itemHeight = height.toFloat() / letters.size
        val centerX = width / 2f

        for (i in letters.indices) {
            val letter = letters[i]
            val y = itemHeight * i + itemHeight / 2f + textPaint.textSize / 3f

            if (letter == activeLetter && isTouching) {
                val scale = 1f + 0.5f * letterAnimProgress
                canvas.save()
                canvas.scale(scale, scale, centerX, y - textPaint.textSize / 3f)
                highlightPaint.alpha = 255
                canvas.drawText(letter, centerX, y, highlightPaint)
                canvas.restore()
            } else {
                textPaint.color = if (isTouching) dimColor else normalColor
                textPaint.alpha = if (isTouching) 130 else 180
                canvas.drawText(letter, centerX, y, textPaint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                isTouching = true
                val index = getLetterIndex(event.y)
                if (index in letters.indices) {
                    val letter = letters[index]
                    if (letter != activeLetter) {
                        activeLetter = letter
                        animateLetterPop()
                        onLetterSelected?.invoke(letter)
                    }
                }
                parent?.requestDisallowInterceptTouchEvent(true)
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isTouching = false
                letterAnimator?.cancel()
                letterAnimProgress = 0f
                activeLetter = null
                parent?.requestDisallowInterceptTouchEvent(false)
                onLetterDeselected?.invoke()
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getLetterIndex(y: Float): Int {
        val itemHeight = height.toFloat() / letters.size
        return (y / itemHeight).toInt().coerceIn(0, letters.size - 1)
    }

    private fun animateLetterPop() {
        letterAnimator?.cancel()
        letterAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 150
            interpolator = OvershootInterpolator(2f)
            addUpdateListener {
                letterAnimProgress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun sp(value: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            value,
            resources.displayMetrics
        )
    }
}
