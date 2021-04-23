package com.github.theapache64.retrosheet.sample.nemo

import com.github.theapache64.retrofit.calladapter.either.EitherCallAdapterFactory
import com.github.theapache64.retrofit.calladapter.flow.FlowResourceCallAdapterFactory
import com.squareup.moshi.Moshi
import com.github.theapache64.retrosheet.RetrosheetInterceptor
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by theapache64 : Jul 21 Tue,2020 @ 02:11
 */
const val POST_PLACE_ORDER = "place_order"
fun main() = runBlocking {

    val retrosheetInterceptor = RetrosheetInterceptor.Builder()
        .setLogging(true)
        .addSheet(
            "products",
            "id", "category_name", "title", "image_url", "is_out_of_stock", "rating", "price"
        )
        .addForm(
            POST_PLACE_ORDER,
            "https://docs.google.com/forms/d/e/1FAIpQLSdPirYGvRLEOB-8osOxnapXZjUBgOcmTIH9J37qOOx_No2ULg/viewform?usp=sf_link"
        )
        .build()

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(retrosheetInterceptor)
        .build()


    val moshi = Moshi.Builder().build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://docs.google.com/spreadsheets/d/1IcZTH6-g7cZeht_xr82SHJOuJXD_p55QueMrZcnsAvQ/")
        .client(okHttpClient)
        .addCallAdapterFactory(EitherCallAdapterFactory())
        .addCallAdapterFactory(FlowResourceCallAdapterFactory())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val nemoApi = retrofit.create(NemoApi::class.java)
    println(nemoApi.getProducts("Category 2"))

    /*  // Adding sample order
      val placeOrder = nemoApi.placeOrder(
          Order(
              "Shifar",
              "Shifar's Villa, 677325",
              """
                      Product 1 - Quantity : 2 - Price : 200
                      Product 2 - Quantity : 4 - Price : 400
                  """.trimIndent(),
              """
                      txnId : 7654534834568345
                      txnFrom : theapache64@ybl
                  """.trimIndent(),
              600
          )
      )

      placeOrder.collect {
          println(it)
      }*/

    // Key Value Config
    // println("Config-> ${nemoApi.getConfig()}")

    Unit
}