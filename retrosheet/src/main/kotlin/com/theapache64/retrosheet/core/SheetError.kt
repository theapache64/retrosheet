package com.theapache64.retrosheet.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/**
 * Created by theapache64 : Jul 25 Sat,2020 @ 14:19
 */
@JsonClass(generateAdapter = true)
data class SheetError(
    @Json(name = "errors")
    val errors: List<ErrorNode>,
    @Json(name = "reqId")
    val reqId: String, // 0
    @Json(name = "status")
    val status: String, // error
    @Json(name = "version")
    val version: String, // 0.6
    @Json(name = "sheet_name")
    var sheetName: String? = null // products
) {
    @JsonClass(generateAdapter = true)
    data class ErrorNode(
        @Json(name = "detailed_message")
        val detailedMessage: String, // Invalid query: NO_COLUMN: Cs
        @Json(name = "human_message")
        var humanMessage: String? = null, // Invalid query: NO_COLUMN: image_urls
        @Json(name = "message")
        val message: String, // INVALID_QUERY
        @Json(name = "reason")
        val reason: String // invalid_query
    )
}