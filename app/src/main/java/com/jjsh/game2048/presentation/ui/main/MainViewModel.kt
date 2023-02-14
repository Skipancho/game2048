package com.jjsh.game2048.presentation.ui.main

import androidx.lifecycle.ViewModel
import com.jjsh.game2048.presentation.ui.view.MoveState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class MainViewModel : ViewModel() {
    private val _blockCount = MutableStateFlow(4)
    val blockCount: StateFlow<Int> get() = _blockCount

    private val _gameMap =
        MutableStateFlow(Array(blockCount.value) { IntArray(blockCount.value) { 0 } })
    val gameMap: StateFlow<Array<IntArray>> get() = _gameMap

    private lateinit var beforeGameMap: List<IntArray>

    val moveAction: (MoveState) -> Boolean = {
        beforeGameMap = gameMap.value.map { array -> array.copyOf() }
        when (it) {
            MoveState.UP -> moveUpAction()
            MoveState.DOWN -> moveDownAction()
            MoveState.RIGHT -> moveRightAction()
            MoveState.LEFT -> moveLeftAction()
        }
        compareMap()
    }

    private fun moveUpAction() {
        val gameMap = this.gameMap.value
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
    }

    private fun moveDownAction() {
        val gameMap = this.gameMap.value
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
    }

    private fun moveLeftAction() {
        val gameMap = this.gameMap.value
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
    }

    private fun moveRightAction() {
        val gameMap = this.gameMap.value
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
    }

    private fun compareMap(): Boolean {
        for (r in beforeGameMap.indices) {
            for (c in beforeGameMap[r].indices) {
                if (beforeGameMap[r][c] != gameMap.value[r][c]) {
                    return true
                }
            }
        }
        return false
    }
}