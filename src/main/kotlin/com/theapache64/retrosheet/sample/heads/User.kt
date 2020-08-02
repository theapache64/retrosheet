package com.theapache64.retrosheet.sample.heads

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/**
 * Generated using MockAPI (https://github.com/theapache64/Mock-API) : Wed Jan 16 14:49:46 UTC 2019
 */
@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "id") val id: String,
    @Json(name = "username") val username: String,
    @Json(name = "api_key") val apiKey: String
)