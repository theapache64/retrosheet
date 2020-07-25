package com.theapache64.retrosheet.sample

import com.theapache64.retrosheet.core.Params
import com.theapache64.retrosheet.core.either.ApiError
import com.theapache64.retrosheet.core.either.Either
import retrofit2.http.GET

interface NemoApi {

    @Params(smartQuery = "SELECT id, title, image_urls")
    @GET("products") // sheetName
    suspend fun getProducts(): Either<ApiError, List<Product>>
}