package io.github.theapache64.retrosheetsample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication
    ) {
        AppUi()
    }
}
