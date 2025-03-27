package io.github.theapache64.retrosheetsample

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    @SerialName("Timestamp")
    val createdAt: String,
    @SerialName("Title")
    val title: String,
    @SerialName("Description")
    val description: String?
)

@Serializable
data class AddNoteRequest(
    @SerialName("Title")
    val title: String,
    @SerialName("Description")
    val description: String?
)
