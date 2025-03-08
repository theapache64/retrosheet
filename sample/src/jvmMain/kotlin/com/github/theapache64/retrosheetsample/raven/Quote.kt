package com.github.theapache64.retrosheetsample.raven

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by theapache64 : Sep 09 Wed,2020 @ 22:09
 */
@Serializable
data class Quote(
    @SerialName("readable_date")
    val date: String,
    @SerialName("quote")
    val quote: String
)
