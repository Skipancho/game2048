package com.jjsh.game2048.presentation.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import com.jjsh.game2048.R
import com.jjsh.game2048.databinding.ActivityMainBinding
import com.jjsh.game2048.presentation.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
    }
}