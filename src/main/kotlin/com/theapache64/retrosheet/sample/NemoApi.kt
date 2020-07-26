package com.theapache64.retrosheet.sample

import com.theapache64.retrosheet.core.SheetQuery
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

interface NemoApi {

    @SheetQuery("SELECT id, title, image_urls")
    @GET("products") // sheetName
    fun getProducts(): Flow<Resource<List<Product>>>

}