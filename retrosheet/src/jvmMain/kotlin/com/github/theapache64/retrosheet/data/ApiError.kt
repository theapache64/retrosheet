package com.github.theapache64.retrosheet.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiError(
    @Json(name = "error_code")
    val errorCode: Int,
    @Json(name = "message")
    val message: String,
    @Json(name = "sheet_error")
    val error: SheetError?
)
