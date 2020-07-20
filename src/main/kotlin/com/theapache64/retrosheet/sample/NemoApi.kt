package com.theapache64.retrosheet.sample

import com.theapache64.retrosheet.Params
import retrofit2.http.GET

interface NemoApi {

    @Params(query = "SELECT A,B,C,D,E WHERE A < 5")
    @GET("products")
    suspend fun getProducts(): ProductsResponse
}