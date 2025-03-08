package com.github.theapache64.retrosheetsample.nemo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by theapache64 : Aug 06 Thu,2020 @ 22:05
 */
@Serializable
data class Order(
    @SerialName("name")
    val name: String,
    @SerialName("address")
    val address: String,
    @SerialName("products")
    val products: String,
    @SerialName("txn_details")
    val txnDetails: String,
    @SerialName("total")
    val total: Long
)
