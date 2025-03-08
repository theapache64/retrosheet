package com.github.theapache64.retrosheetsample.notes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Created by theapache64 : Aug 29 Sat,2020 @ 10:13
 */
@Serializable
data class Note(
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String
)
