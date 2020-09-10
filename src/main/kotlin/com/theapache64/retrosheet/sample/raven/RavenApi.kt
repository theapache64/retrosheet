package com.theapache64.retrosheet.sample.raven

import com.theapache64.retrosheet.core.Read
import com.theapache64.retrosheet.sample.raven.Quote
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by theapache64 : Sep 09 Wed,2020 @ 22:02
 */
interface RavenApi {

    @Read("SELECT * WHERE quote_date = :currentDate")
    @GET("quotes")
    suspend fun getQuote(@Query("currentDate") currentDate: String): Quote
}