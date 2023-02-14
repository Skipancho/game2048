package com.jjsh.game2048.presentation.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    private val _blockCount = MutableStateFlow(4)
    val blockCount: StateFlow<Int> get() = _blockCount

    private val _gameMap = MutableStateFlow(Array(blockCount.value) { IntArray(blockCount.value) { 0 } })
    val gameMap: StateFlow<Array<IntArray>> get() = _gameMap
}