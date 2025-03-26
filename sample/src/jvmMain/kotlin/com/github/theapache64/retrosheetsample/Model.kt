package com.github.theapache64.retrosheetsample

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String
)

@Serializable
data class AddNoteRequest(
    @SerialName("Title")
    val title: String,
    @SerialName("Description")
    val description: String
)
