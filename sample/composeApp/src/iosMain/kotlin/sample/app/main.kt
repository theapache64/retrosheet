import androidx.compose.ui.window.ComposeUIViewController
import io.github.theapache64.retrosheetsample.AppUi
import platform.UIKit.UIViewController


fun MainViewController(): UIViewController = ComposeUIViewController { AppUi() }