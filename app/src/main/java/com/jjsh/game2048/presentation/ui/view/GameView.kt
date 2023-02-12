package com.jjsh.game2048.presentation.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.jjsh.game2048.presentation.ui.common.Colors

class GameView(
    context: Context,
    attributeSet: AttributeSet? = null
) : View(context, attributeSet) {

    private var boardWidth: Int = 0
    private var viewHeight: Int = 0
    private var blockSize: Int = 0

    private lateinit var paint: Paint

    private lateinit var backgroundBlocks: Array<Array<RectF>>

    private lateinit var gameBlocks: Array<Array<GameBlock?>>
    private lateinit var gameNumbers: Array<IntArray>

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

        paint = Paint()
        paint.isAntiAlias = true

        createBackgroundBlocks()
        createGameData()
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

    private fun createGameData() {
        gameBlocks = Array(BLOCK_COUNT) { Array(BLOCK_COUNT) { null } }
        gameNumbers = Array(BLOCK_COUNT) { IntArray(BLOCK_COUNT) { 0 } }

        createNewNumber()
    }

    private fun createNewNumber() {
        val (r, c) = getRandomPosition()
        if (r == -1) return

        gameNumbers[r][c] = 2

        makeGameBlocksFromNumbers()
    }

    private fun makeGameBlocksFromNumbers() {
        for (r in gameNumbers.indices) {
            for (c in gameNumbers[r].indices) {
                gameBlocks[r][c] = if (gameNumbers[r][c] > 0)
                    GameBlock(blockSize, gameNumbers[r][c], r, c)
                else null
            }
        }
    }

    private fun getRandomPosition(): Pair<Int, Int> {
        for (idx in 0 until BLOCK_COUNT * BLOCK_COUNT) {
            val r = idx / BLOCK_COUNT
            val c = idx % BLOCK_COUNT
            if (gameNumbers[r][c] == 0) {
                return Pair(r, c)
            }
        }
        return Pair(-1, -1)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(Colors.RED[Colors.IDX_100])

        drawBackground(canvas)

        drawGameBlocks(canvas)

        invalidate()
    }

    private fun drawBackground(c: Canvas) {
        if (backgroundBlocks.isNotEmpty()) {
            paint.color = Colors.RED[Colors.IDX_200]
            for (blocks in backgroundBlocks) {
                for (block in blocks) {
                    c.drawRoundRect(block, 14f, 14f, paint)
                }
            }
        }
    }

    private fun drawGameBlocks(c: Canvas) {
        if (gameBlocks.isNotEmpty()) {
            for (blocks in gameBlocks){
                for (block in blocks) {
                    block?.draw(c)
                }
            }
        }
    }

    companion object {
        const val SPACING = 2
        const val BLOCK_COUNT = 4
    }
}