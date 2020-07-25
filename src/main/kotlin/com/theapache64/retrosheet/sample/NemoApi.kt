package com.theapache64.retrosheet.sample

import com.theapache64.retrosheet.core.Query
import com.theapache64.retrosheet.core.either.ApiError
import com.theapache64.retrosheet.core.either.Either
import retrofit2.http.GET

interface NemoApi {

    @Query("SELECT id, title, image_url")
    @GET("products") // sheetName
    suspend fun getProducts(): Either<ApiError, List<Product>>

    @Query("SELECT id, title, image_url")
    @GET("products") // sheetName
    suspend fun getProductsList(): List<Product>
}