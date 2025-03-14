package com.github.theapache64.retrosheetsample.raven

import com.github.theapache64.retrosheet.core.RequestInterceptor
import com.github.theapache64.retrosheet.core.RetrosheetConverter
import com.github.theapache64.retrosheetsample.jsonConfig
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking


/**
 * Created by theapache64 : Jul 21 Tue,2020 @ 02:11
 */
fun main() = runBlocking {

    val ktorClient = HttpClient {
        install(RequestInterceptor) {
            isLoggingEnabled = true
            addSheet(
                "quotes",
                "readable_date", "quote_id", "category", "quote"
            )
        }
        install(ContentNegotiation) {
            json(jsonConfig)
        }
    }

    val ktorfit = Ktorfit.Builder()
        .baseUrl("https://docs.google.com/spreadsheets/d/1eDOjClNJGgrROftn9zW69WKNOnQVor_zrF8yo0v5KGs/")
        .httpClient(ktorClient)
        .converterFactories(RetrosheetConverter(jsonConfig))
        .build()

    val api = ktorfit.createRavenApi()
    println(api.getQuote(13042021))
}
