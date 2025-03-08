package com.github.theapache64.retrosheetsample.nemo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by theapache64 : Aug 02 Sun,2020 @ 16:32
 */
@Serializable
data class Config(
    @SerialName("total_products")
    val totalProducts: Int, // 34
    @SerialName("products_per_page")
    val productsPerPage: Int, // 10
    @SerialName("currency")
    val currency: String,
    @SerialName("delivery_charge")
    val deliveryCharge: Int,
    @SerialName("total_pages")
    val totalPages: Int // 4
)
