package com.jjsh.game2048.domain.usecase

import java.util.*
import javax.inject.Inject

class MoveNumbersUseCase @Inject constructor() {
    fun moveUpAction(gameMap: Array<IntArray>) {
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

    fun moveDownAction(gameMap: Array<IntArray>) {
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

    fun moveLeftAction(gameMap: Array<IntArray>) {
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

    fun moveRightAction(gameMap: Array<IntArray>) {
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
}