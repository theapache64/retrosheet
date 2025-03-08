package com.github.theapache64.retrosheetsample.boil

import com.github.theapache64.retrosheet.RetrosheetInterceptor
import com.github.theapache64.retrosheetsample.calladapter.flow.FlowResourceCallAdapterFactory
import com.github.theapache64.retrosheetsample.jsonConverter
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

fun main() = runBlocking {

    val retrosheetInterceptor = RetrosheetInterceptor.Builder()
        .setLogging(true)
        .addSheet(
            "groups",
            "id", "group_name", "files", "instructions"
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
        .addConverterFactory(jsonConverter)
        .build()

    val api = retrofit.create(Api::class.java)
    api.getGroup("retrofit-flow").collect {
        println(it)
    }
}
