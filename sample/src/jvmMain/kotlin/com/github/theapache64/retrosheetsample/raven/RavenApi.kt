package com.github.theapache64.retrosheetsample.raven

import com.github.theapache64.retrosheet.annotations.Read
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by theapache64 : Sep 09 Wed,2020 @ 22:02
 */
interface RavenApi {

    @Read("SELECT * WHERE quote_id = ':quoteId'")
    @GET("quotes")
    suspend fun getQuote(@Query("quoteId") quoteId: Int): Quote
}
