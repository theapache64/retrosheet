package io.github.theapache64.retrosheetsample

import de.jensklingenberg.ktorfit.Ktorfit
import io.github.theapache64.retrosheet.core.RetrosheetConfig
import io.github.theapache64.retrosheet.core.RetrosheetConverter
import io.github.theapache64.retrosheet.core.createRetrosheetPlugin
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal const val SHEET_NAME = "notes"
internal const val ADD_NOTE_ENDPOINT = "add_note"


@Serializable
data class Note(
    @SerialName("Title")
    val title: String,
    @SerialName("Description")
    val description: String?,
    @SerialName("Timestamp")
    val createdAt: String? = null,
)

fun createNotesApi(
    configBuilder: RetrosheetConfig.Builder.() -> Unit = {}
): NotesApi {
    val config = RetrosheetConfig.Builder()
        .apply { this.configBuilder() }
        .setLogging(true)
        // To Read
        .addSheet(
            SHEET_NAME, // sheet name
            "created_at", "title", "description" // columns in same order
        )
        // To write
        .addForm(
            ADD_NOTE_ENDPOINT,
            // Google form name
            "https://docs.google.com/forms/d/e/1FAIpQLSdmavg6P4eZTmIu-0M7xF_z-qDCHdpGebX8MGL43HSGAXcd3w/viewform?usp=sf_link"
        )
        .build()

    val ktorClient = HttpClient {
        install(createRetrosheetPlugin(config)) {}
        install(ContentNegotiation) {
            json()
        }
    }

    val retrofit = Ktorfit.Builder()
        // GoogleSheet Public URL
        .baseUrl("https://docs.google.com/spreadsheets/d/1YTWKe7_mzuwl7AO1Es1aCtj5S9buh3vKauKCMjx1j_M/")
        .httpClient(ktorClient)
        .converterFactories(RetrosheetConverter(config))
        .build()

    return retrofit.createNotesApi()
}