package com.github.theapache64.retrosheet.sample.nemo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/**
 * Created by theapache64 : Jul 17 Fri,2020 @ 21:26
 * Copyright (c) 2020
 * All rights reserved
 */
@JsonClass(generateAdapter = true)
data class Product(
    @Json(name = "id")
    val id: Int, // 1
    @Json(name = "title")
    val title: String, // Guppy
    @Json(name = "image_url")
    val imageUrl: String
)