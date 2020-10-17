package com.theapache64.retrosheet

import com.squareup.moshi.Types
import com.theapache64.retrosheet.core.*
import com.theapache64.retrosheet.utils.*
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
    val isLoggingEnabled: Boolean = false,
    private val sheets: Map<String, Map<String, String>>,
    val forms: Map<String, String>
) : Interceptor {


    companion object {
        private val TAG = RetrosheetInterceptor::class.java.simpleName
        private const val URL_START = "https://docs.google.com/spreadsheets/d"
        private const val ERROR_NO_COLUMN_START = "Invalid query: NO_COLUMN"
        private const val SIGNATURE_LIST_CONTAINS = "java/util/List"
        private const val PACKAGE_LIST_CONTAINS = "java.util.List"
        private const val TYPE_OBJECT = "class java.lang.Object"
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
            val genericReturnType = method?.genericReturnType?.toString()
            return if (genericReturnType != null && genericReturnType != TYPE_OBJECT) {
                genericReturnType.contains(PACKAGE_LIST_CONTAINS)
            } else {
                // go for hard reflection
                try {
                    val f = Method::class.java.getDeclaredField("signature")
                    f.isAccessible = true
                    val signature = f.get(method).toString()
                    signature.contains(SIGNATURE_LIST_CONTAINS)
                } catch (e: NoSuchFieldException) {
                    false
                }
            }

        }

    }

    class Builder {
        private val sheets = mutableMapOf<String, Map<String, String>>()
        private val forms = mutableMapOf<String, String>()
        private var isLoggingEnabled: Boolean = false

        fun build(): RetrosheetInterceptor {
            return RetrosheetInterceptor(
                isLoggingEnabled,
                sheets,
                forms
            )
        }

        fun setLogging(isLoggingEnabled: Boolean): Builder {
            this.isLoggingEnabled = isLoggingEnabled
            return this
        }

        fun addSheet(sheetName: String, columnMap: Map<String, String>): Builder {
            SheetVerifier(columnMap).verify()
            this.sheets[sheetName] = columnMap
            return this
        }

        /**
         * Columns should be in order
         */
        fun addSheet(sheetName: String, vararg columns: String): Builder {
            return addSheet(
                sheetName,
                SheetUtils.toLetterMap(*columns)
            )
        }

        fun addForm(endPoint: String, formLink: String): Builder {
            if (endPoint.contains('/')) {
                throw java.lang.IllegalArgumentException("Form endPoint name cannot contains '/'. Found '$endPoint'")
            }
            forms[endPoint] = formLink
            return this
        }
    }


    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return when {

            GoogleFormHelper.isGoogleFormSubmit(request) -> {
                GoogleFormHelper(chain, request, this)
                    .getFormResponse()
            }

            isRetrosheetUrl(request.url()) -> {
                getRetrosheetResponse(chain, request)
            }

            else -> {
                chain.proceed(request)
            }
        }
    }


    private fun getRetrosheetResponse(chain: Interceptor.Chain, request: Request): Response {
        val newRequest = getModifiedRequest(request)
        val response = chain.proceed(newRequest.second)
        var responseBody = response.body()?.string()
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
                .message(apiError.message)
        } else {

            // It's the CSV.

            // Check if it's a KeyValue pair body
            val isKeyValue = request.tag(Invocation::class.java)?.method()?.getAnnotation(KeyValue::class.java) != null
            if (isKeyValue) {
                if (isLoggingEnabled) {
                    println("$TAG : Transforming body to KeyValue")
                }
                responseBody = KeyValueUtils.transform(responseBody)
            }

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