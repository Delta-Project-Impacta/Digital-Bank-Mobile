package com.domleondev.deltabank.viewModel

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class QRCodeViewModel (context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val transparentPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
    }

    private val borderPaint = Paint().apply {
        color = Color.parseColor("#FF5800")
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }

    var scanWidthDp: Float = 280f
    var scanHeightDp: Float = 280f

    private fun Float.toPx() = this * resources.displayMetrics.density

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val saveCount = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)

        canvas.drawColor(Color.parseColor("#80000000"))

        val sizeW = scanWidthDp.toPx()
        val sizeH = scanHeightDp.toPx()

        val left = (width - sizeW) / 2f
        val top = (height - sizeH) / 2f
        val right = left + sizeW
        val bottom = top + sizeH

        canvas.drawRect(left, top, right, bottom, transparentPaint)

        canvas.drawRect(left, top, right, bottom, borderPaint)

        canvas.restoreToCount(saveCount)
    }
}