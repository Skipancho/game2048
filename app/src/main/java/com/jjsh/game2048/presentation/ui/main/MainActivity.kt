package com.jjsh.game2048.presentation.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.jjsh.game2048.R
import com.jjsh.game2048.databinding.ActivityMainBinding
import com.jjsh.game2048.presentation.base.BaseActivity
import com.jjsh.game2048.presentation.ui.view.GameState
import com.jjsh.game2048.presentation.ui.view.MoveState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
                    AlertDialog.Builder(this)
                        .setMessage(getString(R.string.msg_refresh))
                        .setNegativeButton(getString(R.string.text_no)) { _, _ ->
                            viewModel.setGameState(GameState.PLAYING)
                        }.setPositiveButton(getString(R.string.text_yes)) { _, _ ->
                            binding.gvGame.refresh()
                            viewModel.setGameState(GameState.PLAYING)
                        }.show()
                }
                GameState.START -> {
                    binding.layoutNotify.isVisible = true
                    binding.tvNotify.text = getString(R.string.text_start)
                    binding.ivStart.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                }
                GameState.PLAYING -> {
                    binding.layoutNotify.isVisible = false
                }
                GameState.FINISH -> {
                    binding.layoutNotify.isVisible = true
                    binding.tvNotify.text = getString(R.string.text_restart)
                    binding.ivStart.setImageResource(R.drawable.ic_baseline_refresh_24)
                }
            }
        }
    }
}