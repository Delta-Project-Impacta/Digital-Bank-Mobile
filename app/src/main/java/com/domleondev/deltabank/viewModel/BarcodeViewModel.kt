package com.domleondev.deltabank.viewModel

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.domleondev.deltabank.R

class BarcodeViewModel @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var scanWidthDp = 800f
    var scanHeightDp = 150f

    private val transparentPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
    }

    private val framePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = ContextCompat.getColor(context, R.color.orange)
        isAntiAlias = true
    }

    private val linePaint = Paint().apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.orange)
        isAntiAlias = true
    }

    private var lineOffset = 0f
    private var direction = 10f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val density = resources.displayMetrics.density
        val boxWidth = scanWidthDp * density
        val boxHeight = scanHeightDp * density

        val left = (width - boxWidth) / 2
        val top = (height - boxHeight) / 2
        val right = left + boxWidth
        val bottom = top + boxHeight

        val rect = RectF(left, top, right, bottom)

        val saveCount = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)

        canvas.drawColor(Color.parseColor("#CC000000"))

        canvas.drawRoundRect(rect, 25f, 25f, transparentPaint)

        canvas.restoreToCount(saveCount)

        canvas.drawRoundRect(rect, 25f, 25f, framePaint)

        val lineY = top + lineOffset
        canvas.drawRect(left + 10f, lineY, right - 10f, lineY + 6f, linePaint)

        lineOffset += direction
        if (lineOffset > boxHeight || lineOffset < 0) direction = -direction

        postInvalidateOnAnimation()
    }
}