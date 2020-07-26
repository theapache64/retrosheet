package com.theapache64.retrosheet.sample

import com.squareup.moshi.Moshi
import com.theapache64.retrosheet.RetrosheetInterceptor
import com.theapache64.retrosheet.sample.flow.FlowResourceCallAdapterFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by theapache64 : Jul 21 Tue,2020 @ 02:11
 */
fun main() = runBlocking {

    val retrosheetInterceptor = RetrosheetInterceptor.Builder()
        .setLogging(false)
        .addSheet(
            "products", mapOf(
                "id" to "A",
                "title" to "B",
                "image_url" to "C",
                "price" to "D",
                "quantity" to "E"
            )
        )
        .build()

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(retrosheetInterceptor)
        .build()


    val moshi = Moshi.Builder().build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://docs.google.com/spreadsheets/d/1IcZTH6-g7cZeht_xr82SHJOuJXD_p55QueMrZcnsAvQ/")
        .client(okHttpClient)
        .addCallAdapterFactory(FlowResourceCallAdapterFactory())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val nemoApi = retrofit.create(NemoApi::class.java)

    nemoApi.getProducts().collect {
        when (it) {
            is Resource.Loading -> {
                println("Loading products...")
            }
            is Resource.Success -> {
                println("Got products... ${it.data}")
            }
            is Resource.Error -> {
                println("Failed to get products : ${it.message}")
            }
        }
    }

    Unit
}