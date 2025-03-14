package com.github.theapache64.retrosheetsample.raven

import com.github.theapache64.retrosheet.annotations.Read
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query


/**
 * Created by theapache64 : Sep 09 Wed,2020 @ 22:02
 */
interface RavenApi {
    @Read("SELECT * WHERE quote_id = ':quoteId'")
    @GET("quotes")
    suspend fun getQuote(@Query("quoteId") quoteId: Int): Quote
}
