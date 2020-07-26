package com.theapache64.retrosheet.sample

import com.theapache64.retrofit.calladapter.either.Either
import com.theapache64.retrosheet.core.SheetQuery
import com.theapache64.retrosheet.core.either.ApiError
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

interface NemoApi {

    @SheetQuery("SELECT id, title, image_url")
    @GET("products") // sheetName
    suspend fun getProducts(): Either<ApiError, List<Product>>

    @SheetQuery("SELECT id, title, image_url")
    @GET("products") // sheetName
    fun getProductsFlow(): Flow<Resource<List<Product>>>
}