package com.theapache64.retrosheet.sample.nemo

import com.theapache64.retrosheet.core.Form
import com.theapache64.retrosheet.core.SheetQuery
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NemoApi {

    @SheetQuery("SELECT id, title, image_url")
    @GET("products") // sheetName
    suspend fun getProducts(): List<Product>

    @Form
    @POST(POST_PLACE_ORDER)
    suspend fun placeOrder(@Body order: Order): Order
}
