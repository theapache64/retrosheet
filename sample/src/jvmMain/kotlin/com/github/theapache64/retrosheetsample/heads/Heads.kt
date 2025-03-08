package com.github.theapache64.retrosheetsample.heads

import com.github.theapache64.retrosheet.RetrosheetInterceptor
import com.github.theapache64.retrosheetsample.calladapter.either.EitherCallAdapterFactory
import com.github.theapache64.retrosheetsample.calladapter.flow.FlowResourceCallAdapterFactory
import com.github.theapache64.retrosheetsample.jsonConverter
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit

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

    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    val retrofit = Retrofit.Builder()
        .baseUrl("https://docs.google.com/spreadsheets/d/1zah1fltkf-kKvu1K3dTn6ODsFxKYFshTxssNw8ehRdY/")
        .client(okHttpClient)
        .addCallAdapterFactory(EitherCallAdapterFactory(json))
        .addCallAdapterFactory(FlowResourceCallAdapterFactory())
        .addConverterFactory(jsonConverter)
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
