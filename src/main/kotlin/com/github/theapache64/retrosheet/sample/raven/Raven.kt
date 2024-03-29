package com.github.theapache64.retrosheet.sample.raven

import com.github.theapache64.retrosheet.RetrosheetInterceptor
import com.squareup.moshi.Moshi
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
        .addSheet(
            "quotes",
            "readable_date", "quote_id", "category", "quote"
        )
        .build()

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(retrosheetInterceptor)
        // .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    val moshi = Moshi.Builder().build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://docs.google.com/spreadsheets/d/1eDOjClNJGgrROftn9zW69WKNOnQVor_zrF8yo0v5KGs/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api = retrofit.create(RavenApi::class.java)
    println(api.getQuote(13042021))
}
