package com.theapache64.retrosheet

import com.theapache64.retrosheet.core.SmartQueryMapVerifier
import com.theapache64.retrosheet.core.UrlBuilder
import com.theapache64.retrosheet.utils.CsvConverter
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by theapache64 : Jul 21 Tue,2020 @ 02:33
 */
class RetrosheetInterceptor
private constructor(
    private val isLoggingEnabled: Boolean = false,
    private val smartQueryMaps: Map<String, Map<String, String>>
) : Interceptor {


    class Builder {
        private val smartQueryMaps = mutableMapOf<String, Map<String, String>>()
        private var isLoggingEnabled: Boolean = false

        fun build(): RetrosheetInterceptor {
            return RetrosheetInterceptor(
                isLoggingEnabled,
                smartQueryMaps
            )
        }

        fun setLogging(isLoggingEnabled: Boolean): Builder {
            this.isLoggingEnabled = isLoggingEnabled
            return this
        }

        fun addSmartQueryMap(sheetName: String, smartQueryMap: Map<String, String>): Builder {
            SmartQueryMapVerifier(smartQueryMap).verify()
            this.smartQueryMaps[sheetName] = smartQueryMap
            return this
        }
    }

    companion object {
        private val TAG = RetrosheetInterceptor::class.java.simpleName
        private const val URL_START = "https://docs.google.com/spreadsheets/d"
        private const val KEY_DATA = "data"
        private const val KEY_ERROR = "error"

        private val URL_REGEX by lazy {
            "https://docs\\.google\\.com/spreadsheets/d/(?<docId>.+)/(?<pageName>.+)?".toRegex()
        }

    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return if (isRetrosheetUrl(request.url())) {
            getResponse(chain, request)
        } else {
            chain.proceed(request)
        }
    }

    private fun getResponse(chain: Interceptor.Chain, request: Request): Response {
        val newRequest = getModifiedRequest(request)
        val response = chain.proceed(newRequest)
        val responseBody = response.body()?.string()
            ?: throw IllegalArgumentException("Failed to get CSV data from '${request.url()}'")
        val joRoot = JSONObject()
        try {
            val errorResponse = JSONObject(responseBody)
            // error in request
            joRoot.put(KEY_ERROR, errorResponse)
        } catch (e: JSONException) {
            // no error
            val joData = CsvConverter.convertCsvToJson(responseBody, newRequest)
            if (isLoggingEnabled) {
                println("$TAG : GET <--- $joData")
            }

            joRoot.put(KEY_DATA, joData)
        }

        println(joRoot)

        return response.newBuilder().body(
            ResponseBody.create(
                MediaType.parse("application/json"),
                joRoot.toString(2)
            )
        ).build()
    }

    /**
     * To modify request with proper URL
     */
    private fun getModifiedRequest(request: Request): Request {

        val url = request.url().toString()
        val matcher =
            URL_REGEX.find(url) ?: throw IllegalArgumentException("URL '$url' doesn't match with expected RegEx")

        // Getting docId from URL
        val docId = matcher.groups[1]?.value
            ?: throw IllegalArgumentException("Couldn't find docId from URL '$url'")

        // Getting page name from URL
        val pageName = matcher.groups[2]?.value
            ?: throw IllegalArgumentException("Couldn't find params from URL '$url'. You must specify the page name")

        // Creating realUrl
        val realUrl = UrlBuilder(
            request,
            docId,
            pageName,
            smartQueryMaps
        ).build()
        if (isLoggingEnabled) {
            println("$TAG : GET --> $realUrl")
        }
        return request.newBuilder()
            .url(realUrl)
            .build()
    }

    private fun isRetrosheetUrl(httpUrl: HttpUrl): Boolean {
        val url = httpUrl.toString()
        return url.startsWith(URL_START)
    }
}