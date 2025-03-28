import io.github.theapache64.retrosheetsample.AppUi
import org.jetbrains.compose.web.renderComposable

fun main() {
    renderComposable(rootElementId = "root") {
        AppUi()
    }
}