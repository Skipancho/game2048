package com.jjsh.game2048.presentation.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.jjsh.game2048.presentation.ui.common.Colors
import timber.log.Timber
import java.util.*
import kotlin.math.abs

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

    private var oldX = 0f
    private var oldY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                oldX = event.x
                oldY = event.y
            }
            MotionEvent.ACTION_UP -> {
                val moveX = oldX - event.x
                val moveY = oldY - event.y
                if (moveX == 0f && moveY == 0f) return false
                if (abs(moveX) > abs(moveY)){
                    if (moveX > 0){
                        moveLeftAction()
                    }else if (moveX < 0) {
                        moveRightAction()
                    }
                }else if (abs(moveX) < abs(moveY)) {
                    if (moveY > 0) {
                        moveUpAction()
                    }else if (moveY < 0){
                        moveDownAction()
                    }
                }
            }
        }

        performClick()
        return true
    }

    private fun moveUpAction() {
        Timber.e("moveUp")
        for (row in gameNumbers) {
            val stack = Stack<Int>()

            for (idx in row.indices){
                if (stack.isNotEmpty() && stack.peek() == row[idx]){
                    stack.add(stack.pop() + row[idx])
                    stack.add(0)
                }else {
                    stack.add(row[idx])
                }
            }

            val newRow = mutableListOf<Int>()
            while (stack.isNotEmpty()) {
                val cur = stack.pop()
                if (cur > 0) {
                    newRow.add(0,cur)
                }
            }

            var newRowIdx = 0
            for (n in newRow){
                row[newRowIdx++] = n
            }
            for (i in newRowIdx .. row.lastIndex) {
                row[i] = 0
            }
        }
        createNewNumber()
    }

    private fun moveDownAction() {
        Timber.e("moveDown")
        for (row in gameNumbers ) {
            val stack = Stack<Int>()

            for (idx in row.lastIndex downTo 0){
                if (stack.isNotEmpty() && stack.peek() == row[idx]){
                    stack.add(stack.pop() + row[idx])
                    stack.add(0)
                }else {
                    stack.add(row[idx])
                }
            }

            val newRow = mutableListOf<Int>()
            while (stack.isNotEmpty()) {
                val cur = stack.pop()
                if (cur > 0) {
                    newRow.add(0,cur)
                }
            }

            var newRowIdx = row.lastIndex
            for (n in newRow){
                row[newRowIdx--] = n
            }

            for (i in newRowIdx downTo 0) {
                row[i] = 0
            }
        }
        createNewNumber()
    }

    private fun moveLeftAction() {
        Timber.e("moveLeft")
        for (c in gameNumbers[0].indices){
            val stack = Stack<Int>()
            for (r in gameNumbers.indices){
                if (stack.isNotEmpty() && stack.peek() == gameNumbers[r][c]){
                    stack.add(stack.pop() + gameNumbers[r][c])
                    stack.add(0)
                }else {
                    stack.add(gameNumbers[r][c])
                }
            }
            val newCol = mutableListOf<Int>()
            while (stack.isNotEmpty()) {
                val cur = stack.pop()
                if (cur > 0) {
                    newCol.add(0,cur)
                }
            }

            var newColIdx = 0
            for (n in newCol){
                gameNumbers[newColIdx++][c] = n
            }

            for (i in newColIdx .. gameNumbers[c].lastIndex) {
                gameNumbers[i][c] = 0
            }
        }
        createNewNumber()
    }

    private fun moveRightAction() {
        Timber.e("moveRight")
        for (c in gameNumbers[0].indices){
            val stack = Stack<Int>()
            for (r in gameNumbers.lastIndex downTo 0){
                if (stack.isNotEmpty() && stack.peek() == gameNumbers[r][c]){
                    stack.add(stack.pop() + gameNumbers[r][c])
                    stack.add(0)
                }else {
                    stack.add(gameNumbers[r][c])
                }
            }
            val newCol = mutableListOf<Int>()
            while (stack.isNotEmpty()) {
                val cur = stack.pop()
                if (cur > 0) {
                    newCol.add(0,cur)
                }
            }

            var newColIdx = gameNumbers[c].lastIndex
            for (n in newCol){
                gameNumbers[newColIdx--][c] = n
            }

            for (i in newColIdx downTo 0) {
                gameNumbers[i][c] = 0
            }
        }
        createNewNumber()
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    companion object {
        const val SPACING = 2
        const val BLOCK_COUNT = 4
    }
}