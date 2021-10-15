package com.github.theapache64.retrosheet.sample.heads

import com.github.theapache64.retrofit.calladapter.either.EitherCallAdapterFactory
import com.github.theapache64.retrofit.calladapter.flow.FlowResourceCallAdapterFactory
import com.github.theapache64.retrosheet.RetrosheetInterceptor
import com.squareup.moshi.Moshi
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
        .setLogging(true)
        .addSheet(
            "users",
            "id", "username", "password", "api_key"
        )
        .build()

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(retrosheetInterceptor)
        .build()

    val moshi = Moshi.Builder().build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://docs.google.com/spreadsheets/d/1zah1fltkf-kKvu1K3dTn6ODsFxKYFshTxssNw8ehRdY/")
        .client(okHttpClient)
        .addCallAdapterFactory(EitherCallAdapterFactory())
        .addCallAdapterFactory(FlowResourceCallAdapterFactory())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val headsApi = retrofit.create(HeadsApi::class.java)
    headsApi.login("john", "12345").collect {
        println(it)
    }

    // List all users
    headsApi.getAllUsers().collect {
        println(it)
    }
}
