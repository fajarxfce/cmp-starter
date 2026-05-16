package com.fajar.kmp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.fajar.kmp.di.startAppKoin

fun main() {
    startAppKoin()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "CMP Starter",
        ) {
            App()
        }
    }
}
