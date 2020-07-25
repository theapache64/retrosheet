package com.theapache64.retrosheet

import com.theapache64.retrosheet.core.SmartQueryMapVerifier
import com.theapache64.retrosheet.core.UrlBuilder
import com.theapache64.retrosheet.core.either.SheetError
import com.theapache64.retrosheet.utils.CsvConverter
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import javax.net.ssl.HttpsURLConnection

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
        private const val KEY_PAGE_NAME = "page_name"
        private const val ERROR_NO_COLUMN_START = "Invalid query: NO_COLUMN"

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
        val response = chain.proceed(newRequest.second)
        val responseBody = response.body()?.string()
            ?: throw IllegalArgumentException("Failed to get CSV data from '${request.url()}'")
        var jsonResp: String
        val responseBuilder = response.newBuilder()
        try {
            jsonResp = JSONObject(responseBody).apply {
                put(KEY_PAGE_NAME, newRequest.first)
            }.toString(2)
            responseBuilder
                .code(HttpsURLConnection.HTTP_BAD_REQUEST)
                .message(jsonResp)
        } catch (e: JSONException) {
            // no error
            jsonResp = CsvConverter.convertCsvToJson(responseBody, newRequest.second).toString(2)
            if (isLoggingEnabled) {
                println("$TAG : GET <--- $jsonResp")
            }
            responseBuilder.code(HttpsURLConnection.HTTP_OK)
        }

        return responseBuilder.body(
            ResponseBody.create(
                MediaType.parse("application/json"),
                jsonResp
            )
        ).build()
    }

    /**
     * To modify request with proper URL
     */
    private fun getModifiedRequest(request: Request): Pair<String, Request> {

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
        val csvRequest = request.newBuilder()
            .url(realUrl)
            .build()

        return Pair(pageName, csvRequest)
    }

    private fun isRetrosheetUrl(httpUrl: HttpUrl): Boolean {
        val url = httpUrl.toString()
        return url.startsWith(URL_START)
    }

    /**
     * Make error more understandable
     */
    fun transformError(error: SheetError?): SheetError? {
        return error?.copy(
            errors = error.errors.map {
                it.humanMessage = translateErrorMessage(error.pageName, it.detailedMessage)
                it
            }
        )
    }

    /**
     * To translate google sheet error message to more understandable form.
     */
    private fun translateErrorMessage(
        pageName: String,
        _detailedMessage: String
    ): String {
        var detailedMessage = _detailedMessage
        if (detailedMessage.startsWith(ERROR_NO_COLUMN_START)) {
            val errorPart = detailedMessage.substring(ERROR_NO_COLUMN_START.length)
            var modErrorPart = errorPart
            smartQueryMaps[pageName]?.let { table ->
                for (entry in table) {
                    if (modErrorPart.contains(entry.value, ignoreCase = true)) {
                        modErrorPart = modErrorPart.replace(entry.value, entry.key)
                        break
                    }
                }
            }

            detailedMessage = ERROR_NO_COLUMN_START + modErrorPart
        }
        return detailedMessage
    }
}