package com.theapache64.retrosheet

import com.theapache64.retrosheet.core.MapVerifier
import com.theapache64.retrosheet.core.UrlBuilder
import com.theapache64.retrosheet.core.either.ApiError
import com.theapache64.retrosheet.core.either.ApiErrorJsonAdapter
import com.theapache64.retrosheet.core.either.SheetErrorJsonAdapter
import com.theapache64.retrosheet.utils.CsvConverter
import com.theapache64.retrosheet.utils.JsonValidator
import com.theapache64.retrosheet.utils.MoshiUtils
import okhttp3.*
import retrofit2.Invocation
import java.lang.reflect.Method
import java.net.HttpURLConnection
import javax.net.ssl.HttpsURLConnection

/**
 * Created by theapache64 : Jul 21 Tue,2020 @ 02:33
 */
class RetrosheetInterceptor
private constructor(
    private val isLoggingEnabled: Boolean = false,
    private val sheets: Map<String, Map<String, String>>
) : Interceptor {


    companion object {
        private val TAG = RetrosheetInterceptor::class.java.simpleName
        private const val URL_START = "https://docs.google.com/spreadsheets/d"
        private const val ERROR_NO_COLUMN_START = "Invalid query: NO_COLUMN"
        private const val SIGNATURE_LIST_START = "(ILkotlin/coroutines/Continuation<-Ljava/util/List<L"
        const val ERROR_UNKNOWN = "Something went wrong"

        private val URL_REGEX by lazy {
            "https://docs\\.google\\.com/spreadsheets/d/(?<docId>.+)/(?<params>.+)".toRegex()
        }

        private val sheetErrorJsonAdapter by lazy {
            SheetErrorJsonAdapter(MoshiUtils.moshi)
        }

        private val apiErrorJsonAdapter by lazy {
            ApiErrorJsonAdapter(MoshiUtils.moshi)
        }


        private fun isReturnTypeList(request: Request): Boolean {
            val method = request.tag(Invocation::class.java)?.method()
            val f = Method::class.java.getDeclaredField("signature")
            f.isAccessible = true
            val signature = f.get(method).toString()
            return signature.startsWith(SIGNATURE_LIST_START)
        }

    }

    class Builder {
        private val pages = mutableMapOf<String, Map<String, String>>()
        private var isLoggingEnabled: Boolean = false

        fun build(): RetrosheetInterceptor {
            return RetrosheetInterceptor(
                isLoggingEnabled,
                pages
            )
        }

        fun setLogging(isLoggingEnabled: Boolean): Builder {
            this.isLoggingEnabled = isLoggingEnabled
            return this
        }

        fun addSmartQueryMap(sheetName: String, smartQueryMap: Map<String, String>): Builder {
            MapVerifier(smartQueryMap).verify()
            this.pages[sheetName] = smartQueryMap
            return this
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
        val jsonRoot: String
        val responseBuilder = response.newBuilder()

        // Checking if it's a JSON response. If yes, it's an error else, it's the CSV.
        if (JsonValidator.isValidJsonObject(responseBody)) {
            // It's the spreadsheet error. let's parse it.

            // Adding human understandable error
            val sheetError = sheetErrorJsonAdapter.fromJson(responseBody)?.apply {
                this.sheetName = newRequest.first
                for (error in errors) {
                    error.humanMessage = translateErrorMessage(this.sheetName!!, error.detailedMessage)
                }
            }

            // Converting back to JSON
            val apiError = ApiError(
                HttpURLConnection.HTTP_BAD_REQUEST,
                sheetError?.errors?.firstOrNull()?.humanMessage ?: ERROR_UNKNOWN,
                sheetError
            )

            jsonRoot = apiErrorJsonAdapter.toJson(apiError)

            // Telling it's an error
            responseBuilder
                .code(HttpsURLConnection.HTTP_BAD_REQUEST)
                .message(jsonRoot)
        } else {

            // It's the CSV.
            val csvJson = CsvConverter.convertCsvToJson(responseBody, isReturnTypeList(request))
            if (csvJson != null) {
                jsonRoot = csvJson
                if (isLoggingEnabled) {
                    println("$TAG : GET <--- $jsonRoot")
                }
                responseBuilder.code(HttpsURLConnection.HTTP_OK)
            } else {
                // no data
                jsonRoot = apiErrorJsonAdapter.toJson(
                    ApiError(
                        HttpURLConnection.HTTP_NOT_FOUND,
                        "No data found",
                        null
                    )
                )
                responseBuilder
                    .code(HttpURLConnection.HTTP_NOT_FOUND)
                    .message("No data found")
            }
        }

        return responseBuilder.body(
            ResponseBody.create(
                MediaType.parse("application/json"),
                jsonRoot
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
        val params = matcher.groups[2]?.value
            ?: throw IllegalArgumentException("Couldn't find params from URL '$url'. You must specify the page name")

        val sheetName = parseSheetName(params)

        // Creating realUrl
        val realUrl = UrlBuilder(
            request,
            docId,
            sheetName,
            params,
            sheets
        ).build()

        if (isLoggingEnabled) {
            println("$TAG : GET --> $realUrl")
        }
        val csvRequest = request.newBuilder()
            .url(realUrl)
            .build()

        return Pair(sheetName, csvRequest)
    }

    private fun parseSheetName(params: String): String {
        return params.split("?")[0]
    }

    private fun isRetrosheetUrl(httpUrl: HttpUrl): Boolean {
        val url = httpUrl.toString()
        return url.startsWith(URL_START)
    }


    /**
     * To translate google sheet error message to more understandable form.
     */
    private fun translateErrorMessage(
        sheetName: String,
        _detailedMessage: String
    ): String {
        var humanMessage = _detailedMessage
        if (humanMessage.startsWith(ERROR_NO_COLUMN_START)) {
            // It's a wrong column problem. Now find the column name and
            val errorPart = humanMessage.substring(ERROR_NO_COLUMN_START.length)
            var modErrorPart = errorPart
            sheets[sheetName]?.let { table ->
                for (entry in table) {
                    if (modErrorPart.contains(entry.value, ignoreCase = true)) {
                        modErrorPart = modErrorPart.replace(entry.value, entry.key)
                        break
                    }
                }
            }

            humanMessage = ERROR_NO_COLUMN_START + modErrorPart
        }
        return humanMessage
    }
}