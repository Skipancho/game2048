package com.jjsh.game2048.presentation.ui.main

import androidx.lifecycle.ViewModel
import com.jjsh.game2048.domain.usecase.MoveNumbersUseCase
import com.jjsh.game2048.presentation.ui.view.GameObserver
import com.jjsh.game2048.presentation.ui.view.MoveState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val moveNumbersUseCase: MoveNumbersUseCase
) : ViewModel(), GameObserver {
    private val _blockCount = MutableStateFlow(4)
    val blockCount: StateFlow<Int> get() = _blockCount

    private val _gameMap =
        MutableStateFlow(Array(blockCount.value) { IntArray(blockCount.value) { 0 } })
    val gameMap: StateFlow<Array<IntArray>> get() = _gameMap

    private val _gameScore = MutableStateFlow<Int>(0)
    val gameScore: StateFlow<Int> get() = _gameScore

    private lateinit var beforeGameMap: List<IntArray>

    val moveAction: (MoveState) -> Boolean = {
        beforeGameMap = gameMap.value.map { array -> array.copyOf() }
        when (it) {
            MoveState.UP -> moveNumbersUseCase.moveUpAction(gameMap.value)
            MoveState.DOWN -> moveNumbersUseCase.moveDownAction(gameMap.value)
            MoveState.RIGHT -> moveNumbersUseCase.moveRightAction(gameMap.value)
            MoveState.LEFT -> moveNumbersUseCase.moveLeftAction(gameMap.value)
        }
        compareMap()
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

    override fun notifyGameScoreChanged() {
        _gameScore.value = gameMap.value.sumOf { it.sum() }
    }
}