package com.github.theapache64.retrosheet.sample.nemo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by theapache64 : Aug 06 Thu,2020 @ 22:05
 */
@JsonClass(generateAdapter = true)
data class Order(
    @Json(name = "name")
    val name: String,
    @Json(name = "address")
    val address: String,
    @Json(name = "products")
    val products: String,
    @Json(name = "txn_details")
    val txnDetails: String,
    @Json(name = "total")
    val total: Long
)
