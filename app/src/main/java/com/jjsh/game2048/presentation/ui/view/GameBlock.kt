package com.jjsh.game2048.presentation.ui.view

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.jjsh.game2048.presentation.ui.common.Colors
import timber.log.Timber

class GameBlock(
    private val blockSize: Int,
    private val number: Int,
    private val x: Int = 0,
    private val y: Int = 0,
    isFirstCreated: Boolean = false
) {

    private val paint: Paint = Paint()
    private val textPaint: Paint = Paint()
    private val rectF: RectF = RectF()

    init {
        val colorIdx = findColorIdx()
        paint.isAntiAlias = true
        paint.color = if (isFirstCreated) Colors.AMBER[colorIdx] else default_colors[colorIdx]

        textPaint.isAntiAlias = true
        textPaint.color = Colors.GREY[9 - colorIdx]
        textPaint.textSize = blockSize.toFloat() / 3
        textPaint.textAlign = Paint.Align.CENTER
    }

    private fun findColorIdx(): Int {
        var cnt = 0
        var num = number
        while (num > 1) {
            num /= 2
            cnt++
        }
        return cnt % 10
    }

    fun draw(c: Canvas) {
        rectF.left = x * blockSize.toFloat() + (x + 1) * GameView.SPACING
        rectF.top = y * blockSize.toFloat() + (y + 1) * GameView.SPACING
        rectF.right = rectF.left + blockSize
        rectF.bottom = rectF.top + blockSize

        c.drawRoundRect(rectF, 14f, 14f, paint)
        c.drawText("$number", rectF.centerX(), (rectF.centerY() * 3 + rectF.bottom) / 4, textPaint)
    }

    companion object {
        val default_colors = Colors.LIGHT_BLUE
    }
}