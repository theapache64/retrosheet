package com.github.theapache64.retrosheet.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by theapache64 : Jul 25 Sat,2020 @ 14:19
 */
@Serializable
data class SheetError(
    @SerialName("errors")
    val errors: List<ErrorNode>,
    @SerialName("reqId")
    val reqId: String, // 0
    @SerialName("status")
    val status: String, // error
    @SerialName("version")
    val version: String, // 0.6
    @SerialName("sheet_name")
    var sheetName: String? = null // products
) {
    @Serializable
    data class ErrorNode(
        @SerialName("detailed_message")
        val detailedMessage: String, // Invalid query: NO_COLUMN: Cs
        @SerialName("human_message")
        var humanMessage: String? = null, // Invalid query: NO_COLUMN: image_urls
        @SerialName("message")
        val message: String, // INVALID_QUERY
        @SerialName("reason")
        val reason: String // invalid_query
    )
}
