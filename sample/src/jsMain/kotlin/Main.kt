import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import io.github.theapache64.retrosheetsample.AddNoteRequest
import io.github.theapache64.retrosheetsample.GOOGLE_SHEET_PUBLIC_URL
import io.github.theapache64.retrosheetsample.Note
import io.github.theapache64.retrosheetsample.createNotesApi
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.marginTop
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Iframe
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Table
import org.jetbrains.compose.web.dom.Tbody
import org.jetbrains.compose.web.dom.Td
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Th
import org.jetbrains.compose.web.dom.Thead
import org.jetbrains.compose.web.dom.Tr
import org.jetbrains.compose.web.renderComposable

fun main() {
    val api = createNotesApi()

    renderComposable(rootElementId = "root") {
        Div(
            attrs = {
                classes("container")
            }
        ) {
            Div(
                attrs = {
                    classes("row")
                }
            ){
                Div(
                    attrs = {
                        classes("col-md-6")
                        style {
                            marginTop(100.px)
                        }
                    }
                ) {
                    var noteTitle by remember { mutableStateOf("") }
                    var noteDescription by remember { mutableStateOf("") }
                    var notes by remember { mutableStateOf<List<Note>>(emptyList()) }
                    var isLoading by remember { mutableStateOf(false) }
                    val scope = rememberCoroutineScope()

                    // Input and buttons row
                    Div(attrs = { classes("d-flex", "gap-2", "mb-3") }) {
                        Input(
                            type = InputType.Text,
                            attrs = {
                                classes("form-control")
                                value(noteTitle)
                                onInput { noteTitle = it.value }
                                placeholder("Enter title")
                            }
                        )

                        Input(
                            type = InputType.Text,
                            attrs = {
                                classes("form-control")
                                value(noteDescription)
                                onInput { noteDescription = it.value }
                                placeholder("Enter description")
                            }
                        )

                        Button(
                            attrs = {
                                classes("btn", "btn-primary")
                                onClick {
                                    if (noteTitle.isNotBlank()) {
                                        scope.launch {
                                            api.addNote(AddNoteRequest(noteTitle, noteDescription))
                                            noteTitle = ""
                                            notes = api.getNotes()
                                        }
                                    }
                                }
                            }
                        ) { Text("Add") }

                        Button(
                            attrs = {
                                classes("btn", "btn-secondary")
                                onClick { scope.launch {
                                    notes = api.getNotes()
                                } }
                            }
                        ) { Text("Refresh") }
                    }

                    // Table
                    Table(
                        attrs = { classes("table", "table-striped") }
                    ) {
                        Thead {
                            Tr {
                                Th { Text("Title") }
                                Th { Text("Description") }
                            }
                        }
                        Tbody {
                            if (isLoading) {
                                Tr {
                                    Td(attrs = { attr("colspan", "3") }) {
                                        Text("Loading...")
                                    }
                                }
                            } else if (notes.isEmpty()) {
                                Tr {
                                    Td(attrs = { attr("colspan", "3") }) {
                                        Text("No notes found")
                                    }
                                }
                            } else {
                                notes.forEach { note ->
                                    Tr {
                                        Td { Text(note.title) }
                                        Td { Text(note.description) }
                                    }
                                }
                            }
                        }
                    }
                }

                Div(
                    attrs = {
                        classes("col-md-6")
                    }
                ) {
                    // Full screen iFrame
                    Iframe(
                        attrs = {
                            attr("src", GOOGLE_SHEET_PUBLIC_URL)
                            attr("width", "100%")
                            attr("height", "6000px")
                            attr("frameborder", "0")
                        }
                    )
                }
            }
        }
    }

}