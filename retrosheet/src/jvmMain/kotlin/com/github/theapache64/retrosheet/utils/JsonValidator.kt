package com.github.theapache64.retrosheet.utils

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

/**
 * Created by theapache64 : Jul 25 Sat,2020 @ 22:00
 */
@JsonClass(generateAdapter = true)
class Empty

object JsonValidator {
    fun isValidJsonObject(input: String, moshi: Moshi): Boolean {
        return try {
            EmptyJsonAdapter(moshi).apply { fromJson(input) }
            true
        } catch (e: Exception) {
            false
        }
    }
}
