package com.github.theapache64.retrosheet.sample.nemo

import com.github.theapache64.retrofit.calladapter.flow.Resource
import com.github.theapache64.retrosheet.annotations.KeyValue
import com.github.theapache64.retrosheet.annotations.Read
import com.github.theapache64.retrosheet.annotations.Write
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NemoApi {

    @Read("SELECT * WHERE category_name = :category_name")
    @GET("products")
    suspend fun getProducts(
        @Query("category_name") categoryName: String
    ): List<Product>

    @Write
    @POST(POST_PLACE_ORDER)
    fun placeOrder(@Body order: Order): Flow<Resource<Order>>

    @KeyValue
    @GET("config")
    suspend fun getConfig(): Config
}
