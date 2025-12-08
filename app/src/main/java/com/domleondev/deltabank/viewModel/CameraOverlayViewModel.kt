package com.domleondev.deltabank.viewModel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.View

class CameraOverlayViewModel(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val transparentPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
    }

    private val borderPaint = Paint().apply {
        color = Color.parseColor("#FF5800") // Orange
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val saveCount = canvas.saveLayer(
            0f, 0f, width.toFloat(), height.toFloat(), null
        )

        canvas.drawColor(Color.parseColor("#80000000")) // Preto 50%

        val size = 280.dp(context)
        val left = (width - size) / 2f
        val top = (height - size) / 2f
        val right = left + size
        val bottom = top + size

        canvas.drawRect(left, top, right, bottom, transparentPaint)

        canvas.drawRect(left, top, right, bottom, borderPaint)

        canvas.restoreToCount(saveCount)
    }

    private fun Int.dp(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}