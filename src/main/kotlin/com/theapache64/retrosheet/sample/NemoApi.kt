package com.theapache64.retrosheet.sample

import com.theapache64.retrosheet.core.Params
import retrofit2.http.GET

interface NemoApi {

    @Params(smartQuery = "SELECT id, title, image_url")
    @GET("products") // sheetName
    suspend fun getProducts(): ProductsResponse
}