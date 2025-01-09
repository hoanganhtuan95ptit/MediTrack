package com.simple.meditrack.utils.span

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan

class RoundedBackgroundSpan(
    private val backgroundColor: Int,
    private val textColor: Int,
    private val cornerRadius: Float,
    private val padding: Float
) : ReplacementSpan() {

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        // Tính chiều rộng của span
        val textWidth = paint.measureText(text, start, end)
        return (textWidth + padding * 2).toInt()
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val textWidth = paint.measureText(text, start, end)

        // Lưu trạng thái ban đầu của Paint
        val originalColor = paint.color

        // Vẽ nền bo tròn
        val rectF = RectF(x, top.toFloat(), x + textWidth + padding * 2, bottom.toFloat())
        paint.color = backgroundColor
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)

        // Vẽ chữ lên trên
        paint.color = textColor
        canvas.drawText(text, start, end, x + padding, y.toFloat(), paint)

        // Khôi phục màu ban đầu
        paint.color = originalColor
    }
}
