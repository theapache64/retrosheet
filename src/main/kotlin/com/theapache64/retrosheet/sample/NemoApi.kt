package com.theapache64.retrosheet.sample

import com.theapache64.retrosheet.core.Params
import retrofit2.http.GET

interface NemoApi {

    @Params(
        smartQuery = "SELECT id, title, image_url, price, quantity WHERE quantity is not null"
    ) // Custom query to return only first 4 items
    @GET("products") // sheetName
    suspend fun getProducts(): ProductsResponse
}