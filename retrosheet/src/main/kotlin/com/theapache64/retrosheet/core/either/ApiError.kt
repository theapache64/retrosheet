package com.theapache64.retrosheet.core.either

data class ApiError(
    val errorCode: Int,
    val message: String,
    val error: SheetError?
)