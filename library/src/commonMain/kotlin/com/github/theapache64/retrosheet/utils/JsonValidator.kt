package com.github.theapache64.retrosheet.utils

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Created by theapache64 : Jul 25 Sat,2020 @ 22:00
 */
@Serializable
internal class Empty

internal object JsonValidator {
    fun isValidJsonObject(input: String, json: Json): Boolean {
        return try {
            json.decodeFromString<Empty>(input)
            true
        } catch (e: Exception) {
            false
        }
    }
}
