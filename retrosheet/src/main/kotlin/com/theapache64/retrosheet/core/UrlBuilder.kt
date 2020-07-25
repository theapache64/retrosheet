package com.theapache64.retrosheet.core

import okhttp3.Request
import retrofit2.Invocation
import java.net.URLEncoder

/**
 * Created by theapache64 : Jul 22 Wed,2020 @ 00:06
 */
class UrlBuilder(
    private val request: Request,
    private val docId: String,
    private val sheetName: String,
    private val sheets: Map<String, Map<String, String>>
) {
    fun build(): String {

        val realUrlBuilder =
            StringBuilder("https://docs.google.com/spreadsheets/d/$docId/gviz/tq?tqx=out:csv&sheet=$sheetName")
        var isQueryAdded = false
        request.tag(Invocation::class.java)?.method()?.getAnnotation(Query::class.java)
            ?.let { params ->

                if (params.query.isNotBlank()) {
                    // has smart query
                    val page = sheets[sheetName]
                        ?: throw IllegalArgumentException("Couldn't find smartQueryMap for pageName '$sheetName'")
                    val realQuery = QueryConverter(
                        params.query,
                        page
                    ).convert()
                    realUrlBuilder.append("&tq=${URLEncoder.encode(realQuery, "UTF-8")}")
                    isQueryAdded = true
                }

            }

        request.tag(Invocation::class.java)?.method()?.getAnnotation(Params::class.java)
            ?.let { params ->

                if (params.range.isNotBlank()) {
                    realUrlBuilder.append("&range=${params.range}")
                }

                if (params.headers != -1) {
                    realUrlBuilder.append("&headers=${params.headers}")
                }

                if (params.rawQuery.isNotBlank()) {
                    require(!isQueryAdded) { "Both rawQuery and @Query cannot work together" }

                    realUrlBuilder.append("&tq=${URLEncoder.encode(params.rawQuery, "UTF-8")}")
                }
            }


        return realUrlBuilder.toString()
    }
}