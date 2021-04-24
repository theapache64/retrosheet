package com.github.theapache64.retrosheet.bugs

import com.github.theapache64.retrosheet.RetrosheetInterceptor
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

const val BASE_URL =  "https://docs.google.com/spreadsheets/d/1PdgQmxOK2l34qo4R27J8ETlH2GUVdtn43UL8TONC-Ss/"

fun main(args: Array<String>) = runBlocking {
    val networkingService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(
            OkHttpClient.Builder()
                .addInterceptor(
                    RetrosheetInterceptor.Builder().run {
                        NozzleStubResponse.addSheet(this)
                        NozzleTypeResponse.addSheet(this)
                    }.build()
                )
                .build()
        )
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(NetworkingService::class.java)

    println(networkingService.getNozzleTypes())
}