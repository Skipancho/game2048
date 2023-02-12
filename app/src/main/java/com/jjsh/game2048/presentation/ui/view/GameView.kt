package com.jjsh.game2048.presentation.ui.view

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class GameView(
    context: Context,
    attributeSet: AttributeSet? = null
) : View(context,attributeSet){

    private var boardWidth: Int = 0
    private var viewHeight: Int = 0
    private var blockSize: Int = 0

    private lateinit var backgroundBlocks: Array<Array<RectF>>

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val height = bottom - top
        val width = right - left
        if (height > 0 && width > 0) {
            val blockSize = (width - (SPACING * (BLOCK_COUNT + 1))) / BLOCK_COUNT
            val w = blockSize * BLOCK_COUNT + SPACING * (BLOCK_COUNT - 1)
            val h = blockSize * BLOCK_COUNT + SPACING * (BLOCK_COUNT + 1)

            initGame(w, h, blockSize)
        }
    }

    private fun initGame(w: Int, h: Int, blockSize: Int) {
        this.blockSize = blockSize
        this.boardWidth = w
        this.viewHeight = h

        createBackgroundBlocks()
    }

    private fun createBackgroundBlocks() {
        backgroundBlocks = Array(BLOCK_COUNT) { i ->
            Array(BLOCK_COUNT) { j ->
                RectF(
                    j * (blockSize + SPACING).toFloat() + SPACING,
                    i * (blockSize + SPACING).toFloat() + SPACING,
                    j * (blockSize + SPACING).toFloat() + blockSize,
                    i * (blockSize + SPACING).toFloat() + blockSize
                )
            }
        }
    }

    companion object {
        const val SPACING = 2
        const val BLOCK_COUNT = 4
    }
}