package com.github.theapache64.retrosheet.sample.nemo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by theapache64 : Aug 02 Sun,2020 @ 16:32
 */
@JsonClass(generateAdapter = true)
data class Config(
    @Json(name = "total_products")
    val totalProducts: Int, // 34
    @Json(name = "products_per_page")
    val productsPerPage: Int, // 10
    @Json(name = "currency")
    val currency: String,
    @Json(name = "delivery_charge")
    val deliveryCharge: Int,
    @Json(name = "total_pages")
    val totalPages: Int // 4
)