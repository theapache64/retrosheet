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
    private val pageName: String,
    private val smartQueryMaps: Map<String, Map<String, String>>
) {
    fun build(): String {

        val realUrl = StringBuilder("https://docs.google.com/spreadsheets/d/$docId/gviz/tq?tqx=out:csv&sheet=$pageName")
        request.tag(Invocation::class.java)?.method()?.getAnnotation(Params::class.java)
            ?.let { params ->

                require(params.query.isBlank() || params.smartQuery.isBlank()) {
                    "query and smartQuery can't be defined at the same time"
                }

                if (params.query.isNotBlank()) {
                    realUrl.append("&tq=${URLEncoder.encode(params.query, "UTF-8")}")
                } else if (params.smartQuery.isNotBlank()) {
                    // has smart query
                    val smartQueryMap = smartQueryMaps[pageName]
                        ?: throw IllegalArgumentException("Couldn't find smartQueryMap for pageName '$pageName'")
                    val realQuery = QueryConverter(
                        params.smartQuery,
                        smartQueryMap
                    ).convert()
                    realUrl.append("&tq=${URLEncoder.encode(realQuery, "UTF-8")}")
                }

                if (params.range.isNotBlank()) {
                    realUrl.append("&range=${params.range}")
                }

                if (params.headers != -1) {
                    realUrl.append("&headers=${params.headers}")
                }
            }
        return realUrl.toString()
    }
}