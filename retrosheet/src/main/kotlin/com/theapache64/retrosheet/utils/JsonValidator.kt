package com.theapache64.retrosheet.utils

import com.squareup.moshi.JsonClass

/**
 * Created by theapache64 : Jul 25 Sat,2020 @ 22:00
 */
@JsonClass(generateAdapter = true)
class Empty

object JsonValidator {
    fun isValidJsonObject(input: String): Boolean {
        return try {
            EmptyJsonAdapter(MoshiUtils.moshi).apply { fromJson(input) }
            true
        } catch (e: Exception) {
            false
        }
    }
}