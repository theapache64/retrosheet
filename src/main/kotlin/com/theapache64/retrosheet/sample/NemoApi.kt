package com.theapache64.retrosheet.sample

import com.theapache64.retrosheet.core.SheetQuery
import retrofit2.http.GET

interface NemoApi {


    @SheetQuery("SELECT id, title, image_url")
    @GET("products") // sheetName
    suspend fun getProducts(): List<Product>
}
