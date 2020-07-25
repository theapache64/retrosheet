package com.theapache64.retrosheet.sample

import com.theapache64.retrosheet.core.SheetQuery
import retrofit2.http.GET
import retrofit2.http.Query

interface NemoApi {

/*    @Query("SELECT id, title, image_url")
    @GET("products") // sheetName
    suspend fun getProducts(): Either<ApiError, List<Product>>

    @Query("SELECT id, title, image_url")
    @GET("products") // sheetName
    suspend fun getProductsList(): List<Product>*/

    @SheetQuery("SELECT id, title, image_url WHERE id = :id")
    @GET("products")
    suspend fun getProduct(
        @Query("id") id: Int
    ): Product

    @GET("products")
    suspend fun getProducts(): List<Product>

    @GET("product")
    suspend fun getProduct(): Product
}