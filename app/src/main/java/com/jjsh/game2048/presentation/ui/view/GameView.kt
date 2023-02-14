package com.jjsh.game2048.presentation.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.databinding.BindingAdapter
import com.jjsh.game2048.presentation.ui.common.Colors
import kotlinx.coroutines.*
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
    private var blockCount: Int = 4

    private lateinit var paint: Paint

    private lateinit var backgroundBlocks: Array<Array<RectF>>

    private lateinit var gameBlocks: Array<Array<GameBlock?>>
    private lateinit var gameMap: Array<IntArray>

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val height = bottom - top
        val width = right - left
        if (height > 0 && width > 0) {
            val blockSize = (width - (SPACING * (blockCount + 1))) / blockCount
            val w = blockSize * blockCount + SPACING * (blockCount - 1)
            val h = blockSize * blockCount + SPACING * (blockCount + 1)

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
        backgroundBlocks = Array(blockCount) { i ->
            Array(blockCount) { j ->
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
        gameBlocks = Array(blockCount) { Array(blockCount) { null } }

        createNewNumber()
    }

    private fun createNewNumber() {
        val (r, c) = getRandomPosition()
        if (r == -1) return

        gameMap[r][c] = -2

        makeGameBlocksFromNumbers()

        CoroutineScope(Dispatchers.Main).launch {
            delay(200)
            makeGameBlocksFromNumbers()
            cancel()
        }
    }

    private fun makeGameBlocksFromNumbers() {
        for (r in gameMap.indices) {
            for (c in gameMap[r].indices) {
                gameBlocks[r][c] = if (gameMap[r][c] > 0)
                    GameBlock(blockSize, gameMap[r][c], r, c)
                else if (gameMap[r][c] < 0) {
                    gameMap[r][c] *= -1
                    GameBlock(blockSize, gameMap[r][c], r, c, true)
                } else null
            }
        }
    }

    private fun getRandomPosition(): Pair<Int, Int> {
        val zeroPosition = mutableListOf<Int>()
        for (idx in 0 until blockCount * blockCount) {
            val r = idx / blockCount
            val c = idx % blockCount
            if (gameMap[r][c] == 0) {
                zeroPosition.add(idx)
            }
        }
        if (zeroPosition.isEmpty()) return Pair(-1, -1)

        val random = zeroPosition.random()
        return Pair(random / blockCount, random % blockCount)
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
            for (blocks in gameBlocks) {
                for (block in blocks) {
                    block?.draw(c)
                }
            }
        }
    }

    private var oldX = 0f
    private var oldY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                oldX = event.x
                oldY = event.y
            }
            MotionEvent.ACTION_UP -> {
                val moveX = oldX - event.x
                val moveY = oldY - event.y
                if (moveX == 0f && moveY == 0f) return false
                if (abs(moveX) > abs(moveY)) {
                    if (moveX > 0) {
                        moveLeftAction()
                    } else if (moveX < 0) {
                        moveRightAction()
                    }
                } else if (abs(moveX) < abs(moveY)) {
                    if (moveY > 0) {
                        moveUpAction()
                    } else if (moveY < 0) {
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
        for (row in gameMap) {
            val stack = Stack<Int>()

            for (idx in row.indices) {
                if (stack.isNotEmpty() && stack.peek() == row[idx]) {
                    stack.add(stack.pop() + row[idx])
                    stack.add(0)
                } else if (row[idx] > 0) {
                    stack.add(row[idx])
                }
            }

            val newRow = mutableListOf<Int>()
            while (stack.isNotEmpty()) {
                val cur = stack.pop()
                if (cur > 0) {
                    newRow.add(0, cur)
                }
            }

            var newRowIdx = 0
            for (n in newRow) {
                row[newRowIdx++] = n
            }
            for (i in newRowIdx..row.lastIndex) {
                row[i] = 0
            }
        }
        createNewNumber()
    }

    private fun moveDownAction() {
        Timber.e("moveDown")
        for (row in gameMap) {
            val stack = Stack<Int>()

            for (idx in row.lastIndex downTo 0) {
                if (stack.isNotEmpty() && stack.peek() == row[idx]) {
                    stack.add(stack.pop() + row[idx])
                    stack.add(0)
                } else if (row[idx] > 0) {
                    stack.add(row[idx])
                }
            }

            val newRow = mutableListOf<Int>()
            while (stack.isNotEmpty()) {
                val cur = stack.pop()
                if (cur > 0) {
                    newRow.add(0, cur)
                }
            }

            var newRowIdx = row.lastIndex
            for (n in newRow) {
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
        for (c in gameMap[0].indices) {
            val stack = Stack<Int>()
            for (r in gameMap.indices) {
                if (stack.isNotEmpty() && stack.peek() == gameMap[r][c]) {
                    stack.add(stack.pop() + gameMap[r][c])
                    stack.add(0)
                } else if (gameMap[r][c] > 0) {
                    stack.add(gameMap[r][c])
                }
            }
            val newCol = mutableListOf<Int>()
            while (stack.isNotEmpty()) {
                val cur = stack.pop()
                if (cur > 0) {
                    newCol.add(0, cur)
                }
            }

            var newColIdx = 0
            for (n in newCol) {
                gameMap[newColIdx++][c] = n
            }

            for (i in newColIdx..gameMap[c].lastIndex) {
                gameMap[i][c] = 0
            }
        }
        createNewNumber()
    }

    private fun moveRightAction() {
        Timber.e("moveRight")
        for (c in gameMap[0].indices) {
            val stack = Stack<Int>()
            for (r in gameMap.lastIndex downTo 0) {
                if (stack.isNotEmpty() && stack.peek() == gameMap[r][c]) {
                    stack.add(stack.pop() + gameMap[r][c])
                    stack.add(0)
                } else if (gameMap[r][c] > 0) {
                    stack.add(gameMap[r][c])
                }
            }
            val newCol = mutableListOf<Int>()
            while (stack.isNotEmpty()) {
                val cur = stack.pop()
                if (cur > 0) {
                    newCol.add(0, cur)
                }
            }

            var newColIdx = gameMap[c].lastIndex
            for (n in newCol) {
                gameMap[newColIdx--][c] = n
            }

            for (i in newColIdx downTo 0) {
                gameMap[i][c] = 0
            }
        }
        createNewNumber()
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun setMap(numbers: Array<IntArray>) {
        gameMap = numbers
    }

    fun setCount(blockCount: Int) {
        this.blockCount = blockCount
    }

    companion object {
        const val SPACING = 2
    }
}

@BindingAdapter("gameMap")
fun GameView.setGameMap(numbers: Array<IntArray>) {
    setMap(numbers)
}

@BindingAdapter("blockCount")
fun GameView.setBlockCount(blockCount: Int) {
    setCount(blockCount)
}