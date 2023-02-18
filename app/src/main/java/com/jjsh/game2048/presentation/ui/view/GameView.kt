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
import kotlin.math.abs

class GameView(
    context: Context,
    attributeSet: AttributeSet? = null
) : View(context, attributeSet) {

    private var boardWidth: Int = 0
    private var viewHeight: Int = 0
    private var verticalPadding: Int = 0
    private var blockSize: Int = 0
    private var blockCount: Int = 4

    private lateinit var paint: Paint

    private lateinit var backgroundBlocks: Array<Array<RectF>>

    private lateinit var gameBlocks: Array<Array<GameBlock?>>
    private lateinit var gameMap: Array<IntArray>

    private lateinit var moveAction: (MoveState) -> Boolean

    private lateinit var gameObserver: GameObserver
    private var gameState: GameState = GameState.START

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val height = bottom - top
        val width = right - left
        if (height > 0 && width > 0) {
            val blockSize = (width - (SPACING * (blockCount + 1))) / blockCount
            val w = blockSize * blockCount + SPACING * (blockCount - 1)
            val h = blockSize * blockCount + SPACING * (blockCount + 1)
            val padding = (height - width) / 2
            initGame(w, h, blockSize, padding)
        }
    }

    private fun initGame(w: Int, h: Int, blockSize: Int, padding: Int) {
        this.blockSize = blockSize
        this.boardWidth = w
        this.viewHeight = h
        this.verticalPadding = padding

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
                    i * (blockSize + SPACING).toFloat() + SPACING + verticalPadding,
                    j * (blockSize + SPACING).toFloat() + blockSize,
                    i * (blockSize + SPACING).toFloat() + blockSize + verticalPadding
                )
            }
        }
    }

    private fun createGameData() {
        gameBlocks = Array(blockCount) { Array(blockCount) { null } }

        createNewNumber()
    }

    private fun createNewNumber() {
        if (gameState != GameState.PLAYING) return
        val (r, c) = getRandomPosition()
        if (r == -1) {
            gameObserver.notifyGameMapFulled()
            return
        }

        gameMap[r][c] = -2

        val loc = makeGameBlocksFromNumbers()

        gameObserver.notifyGameScoreChanged()

        CoroutineScope(Dispatchers.Main).launch {
            delay(200)
            gameBlocks[loc.first][loc.second] =
                GameBlock(blockSize, gameMap[r][c], r, c, verticalPadding)
            cancel()
        }
    }

    private fun makeGameBlocksFromNumbers(): Pair<Int, Int> {
        var (i, j) = Pair(-1, -1)
        for (r in gameMap.indices) {
            for (c in gameMap[r].indices) {
                gameBlocks[r][c] = if (gameMap[r][c] > 0)
                    GameBlock(blockSize, gameMap[r][c], r, c, verticalPadding)
                else if (gameMap[r][c] < 0) {
                    i = r
                    j = c
                    gameMap[r][c] *= -1
                    GameBlock(blockSize, gameMap[r][c], r, c, verticalPadding, true)
                } else null
            }
        }
        return Pair(i, j)
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

        //canvas.drawColor(Colors.RED[Colors.IDX_100])

        if (gameState == GameState.PLAYING || gameState == GameState.FINISH) {
            drawBackground(canvas)
            drawGameBlocks(canvas)
        }

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
        if (gameState != GameState.PLAYING) return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                oldX = event.x
                oldY = event.y
            }
            MotionEvent.ACTION_UP -> {
                val moveX = oldX - event.x
                val moveY = oldY - event.y
                if (moveX == 0f && moveY == 0f) return false
                val createNew = when {
                    abs(moveX) > abs(moveY) ->
                        when {
                            moveX > 0 -> moveAction(MoveState.LEFT)
                            moveX < 0 -> moveAction(MoveState.RIGHT)
                            else -> false
                        }
                    abs(moveX) < abs(moveY) ->
                        when {
                            moveY > 0 -> moveAction(MoveState.UP)
                            moveY < 0 -> moveAction(MoveState.DOWN)
                            else -> false
                        }
                    else -> false
                }
                if (createNew) createNewNumber()
                else {
                    checkMapFulled()
                    makeGameBlocksFromNumbers()
                }

                performClick()
            }
        }
        return true
    }

    private fun checkMapFulled() {
        if (gameMap.find { array -> array.find { it == 0 } != null } != null) return
        gameObserver.notifyGameMapFulled()
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

    fun setMoveAction(moveAction: (MoveState) -> Boolean) {
        this.moveAction = moveAction
    }

    fun setObserver(gameObserver: GameObserver) {
        this.gameObserver = gameObserver
    }

    fun setState(gameState: GameState) {
        this.gameState = gameState
    }

    fun refresh() {
        for (r in gameMap.indices) {
            for (c in gameMap[r].indices) {
                gameMap[r][c] = 0
            }
        }
        createNewNumber()
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

@BindingAdapter("onMove")
fun GameView.setOnMoveEvent(moveAction: (MoveState) -> Boolean) {
    setMoveAction(moveAction)
}

@BindingAdapter("gameObserver")
fun GameView.setGameObserver(gameObserver: GameObserver) {
    setObserver(gameObserver)
}

@BindingAdapter("gameState")
fun GameView.setGameState(gameState: GameState) {
    setState(gameState)
}

enum class MoveState {
    UP, DOWN, LEFT, RIGHT
}

enum class GameState {
    RESTART, START, PLAYING, FINISH
}

interface GameObserver {
    fun notifyGameScoreChanged()
    fun notifyGameMapFulled()
}

