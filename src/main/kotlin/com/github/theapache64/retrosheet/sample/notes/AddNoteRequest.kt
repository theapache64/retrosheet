package com.github.theapache64.retrosheet.sample.notes

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by theapache64 : Aug 29 Sat,2020 @ 10:13
 */
@JsonClass(generateAdapter = true)
data class AddNoteRequest(
    @Json(name = "Title")
    val title: String,
    @Json(name = "Description")
    val description: String
)