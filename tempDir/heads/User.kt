package com.github.theapache64.retrosheetsample.heads

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Generated using MockAPI (https://github.com/theapache64/Mock-API) : Wed Jan 16 14:49:46 UTC 2019
 */
@Serializable
data class User(
    @SerialName("id") val id: String,
    @SerialName("username") val username: String,
    @SerialName("api_key") val apiKey: String
)
