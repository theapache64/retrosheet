package com.github.theapache64.retrosheetsample

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

internal val jsonConverter = json.asConverterFactory("application/json; charset=UTF8".toMediaType())

