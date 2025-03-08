package com.github.theapache64.retrosheet.core

import com.github.theapache64.retrosheet.annotations.Read
import com.github.theapache64.retrosheet.annotations.SheetParams
import java.net.URLEncoder
import okhttp3.Request
import retrofit2.Invocation

/**
 * Created by theapache64 : Jul 22 Wed,2020 @ 00:06
 */
class UrlBuilder(
    private val request: Request,
    private val docId: String,
    private val sheetName: String,
    private val params: String,
    private val queryMap: Map<String, String>
) {
    fun build(): String {

        val paramMap = convertToMap(params)

        val realUrlBuilder =
            StringBuilder("https://docs.google.com/spreadsheets/d/$docId/gviz/tq?tqx=out:csv&sheet=$sheetName")
        var isQueryAdded = false
        request.tag(Invocation::class.java)?.method()?.getAnnotation(Read::class.java)
            ?.let { params: Read ->

                if (params.query.isNotBlank()) {
                    // has smart query
                    val realQuery = QueryConverter(
                        params.query,
                        queryMap,
                        paramMap
                    ).convert()
                    realUrlBuilder.append("&tq=$realQuery")
                    isQueryAdded = true
                }
            }

        request.tag(Invocation::class.java)?.method()?.getAnnotation(SheetParams::class.java)
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

    private fun convertToMap(params: String): Map<String, String>? {
        return if (params.contains("?")) {
            val x = params.substring(params.indexOf('?') + 1)
            val paramMap = mutableMapOf<String, String>()
            for (paramPair in x.split("&")) {
                val paramSplit = paramPair.split("=")
                if (paramSplit.size >= 2) {
                    val key = paramSplit[0]
                    val value = paramPair.substring(key.length + 1)
                    paramMap[key] = value
                }
            }
            paramMap
        } else {
            null
        }
    }
}
