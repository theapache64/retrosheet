package com.github.theapache64.retrosheetsample.nemo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by theapache64 : Jul 17 Fri,2020 @ 21:26
 * Copyright (c) 2020
 * All rights reserved
 */
@Serializable
data class Product(
    @SerialName("id")
    val id: Int, // 1
    @SerialName("title")
    val title: String, // Guppy
    @SerialName("image_url")
    val imageUrl: String
)
