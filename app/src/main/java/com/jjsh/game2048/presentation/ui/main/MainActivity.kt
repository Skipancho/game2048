package com.jjsh.game2048.presentation.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.jjsh.game2048.R
import com.jjsh.game2048.databinding.ActivityMainBinding
import com.jjsh.game2048.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel

        observeGameState()
    }

    private fun observeGameState() {
        observeFlowWithLifecycle(viewModel.gameState) {
            when(it) {
                GameState.RESTART -> {
                    binding.gvGame.refresh()
                    viewModel.setGameState(GameState.START)
                }
                GameState.START -> {
                    viewModel.setGameState(GameState.PLAYING)
                }
                GameState.PLAYING -> {
                    //none
                }
                GameState.FINISH -> {
                    // todo. finish action
                    Toast.makeText(this, "끝", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}