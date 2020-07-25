package com.theapache64.retrosheet.sample

import com.theapache64.retrosheet.RetrosheetInterceptor
import com.theapache64.retrosheet.sample.core.EitherCallAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by theapache64 : Jul 21 Tue,2020 @ 02:11
 */
fun main() = runBlocking {

    val retrosheetInterceptor = RetrosheetInterceptor.Builder()
        .setLogging(true)
        .addSmartQueryMap(
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

    val retrofit = Retrofit.Builder()
        .baseUrl("https://docs.google.com/spreadsheets/d/1IcZTH6-g7cZeht_xr82SHJOuJXD_p55QueMrZcnsAvQ/")
        .client(okHttpClient)
        .addCallAdapterFactory(object : EitherCallAdapterFactory<String>() {
            override fun parse(): String? {
                return "Some shit!"
            }
        })
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val nemoApi = retrofit.create(NemoApi::class.java)
    val productsResp = nemoApi.getProducts()

    productsResp.fold({
        println("Error: ${it.message}")
    }, {
        println("Yey!!! Got products! : ${it.size}")
    })

}