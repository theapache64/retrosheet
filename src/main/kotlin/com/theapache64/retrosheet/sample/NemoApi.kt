package com.theapache64.retrosheet.sample

import com.theapache64.retrosheet.core.Params
import com.theapache64.retrosheet.sample.core.ApiError
import com.theapache64.retrosheet.sample.core.Either
import retrofit2.http.GET

interface NemoApi {

    @Params(smartQuery = "SELECT id, title, image_urls")
    @GET("products") // sheetName
    suspend fun getProducts(): Either<ApiError, List<Product>>
}