package com.theapache64.retrosheet.sample

import com.theapache64.retrosheet.Params
import retrofit2.http.GET

interface NemoApi {

    @Params(query = "SELECT A,B,C,D,E WHERE A < 5") // Custom query to return only first 4 items
    @GET("products") // sheetName
    suspend fun getProducts(): ProductsResponse
}