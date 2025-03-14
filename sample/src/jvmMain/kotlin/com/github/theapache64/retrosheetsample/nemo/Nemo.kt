package com.github.theapache64.retrosheetsample.nemo

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

    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    val retrofit = Retrofit.Builder()
        .baseUrl("https://docs.google.com/spreadsheets/d/1IcZTH6-g7cZeht_xr82SHJOuJXD_p55QueMrZcnsAvQ/")
        .client(okHttpClient)
        .addCallAdapterFactory(EitherCallAdapterFactory(json))
        .addCallAdapterFactory(FlowResourceCallAdapterFactory())
        .addConverterFactory(jsonConverter)
        .build()

    val nemoApi = retrofit.create(NemoApi::class.java)
    println(nemoApi.getProducts("Category 2"))

    // Adding sample order
    val placeOrder = nemoApi.placeOrder(
        Order(
            name = "Shifar",
            address = "Shifar's Villa, 677325",
            products = """
                                  Product 1 - Quantity : 2 - Price : 200
                                  Product 2 - Quantity : 4 - Price : 400
            """.trimIndent(),
            txnDetails = """
                                  txnId : 7654534834568345
                                  txnFrom : theapache64@ybl
            """.trimIndent(),
            total = 600
        )
    )

    placeOrder.collect {
        println(it)
    }

    // Key Value Config
    // println("Config-> ${nemoApi.getConfig()}")

    Unit
}
