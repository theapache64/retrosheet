package com.github.theapache64.retrosheetsample

import kotlinx.serialization.json.Json

internal val jsonConfig = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

