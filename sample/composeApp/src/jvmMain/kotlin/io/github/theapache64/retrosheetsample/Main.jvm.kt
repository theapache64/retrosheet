package io.github.theapache64.retrosheetsample

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import org.jetbrains.compose.reload.DevelopmentEntryPoint

fun main() = singleWindowApplication(
    title = "Retrosheet Desktop Sample",
    state = WindowState(width = 1800.dp, height = 800.dp),
    alwaysOnTop = true
) {
    DevelopmentEntryPoint {
        AppUi()
    }
}
