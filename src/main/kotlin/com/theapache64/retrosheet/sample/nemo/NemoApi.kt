package com.theapache64.retrosheet.sample.nemo

import com.theapache64.retrofit.calladapter.flow.Resource
import com.theapache64.retrosheet.core.Write
import com.theapache64.retrosheet.core.Read
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NemoApi {

    @Read("SELECT id, title, image_url")
    @GET("products") // sheetName
    suspend fun getProducts(): List<Product>

    @Write
    @POST(POST_PLACE_ORDER)
    fun placeOrder(@Body order: Order): Flow<Resource<Order>>
}
