package com.fajar.kmp

import androidx.compose.ui.window.ComposeUIViewController
import com.fajar.kmp.di.startAppKoin

fun MainViewController() = run {
    startAppKoin()
    ComposeUIViewController { App() }
}
