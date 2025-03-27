package io.github.theapache64.retrosheet.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    @SerialName("error_code")
    val errorCode: Int,
    @SerialName("message")
    val message: String,
    @SerialName("sheet_error")
    val error: SheetError?
)
