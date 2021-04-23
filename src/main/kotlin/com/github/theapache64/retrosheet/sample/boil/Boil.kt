package com.github.theapache64.retrosheet.sample.boil

import com.github.theapache64.retrofit.calladapter.flow.FlowResourceCallAdapterFactory
import com.github.theapache64.retrosheet.RetrosheetInterceptor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

fun main(args: Array<String>) = runBlocking {

    val retrosheetInterceptor = RetrosheetInterceptor.Builder()
        .setLogging(true)
        .addSheet(
            "groups",
            "id", "group_name", "classes"
        )
        .build()

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(retrosheetInterceptor)
        .build()

    val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://docs.google.com/spreadsheets/d/1OF384yi-k3iBgiyLnhYDAoYAV8wJGCh2yEqm3MfQQko/")
        .addCallAdapterFactory(FlowResourceCallAdapterFactory())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val api = retrofit.create(Api::class.java)
    api.getGroup("retrofit-flow").collect {
        println(it)
    }
}