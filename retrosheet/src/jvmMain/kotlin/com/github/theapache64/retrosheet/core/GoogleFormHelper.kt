package com.github.theapache64.retrosheet.core

import com.github.theapache64.retrosheet.RetrosheetInterceptor
import com.github.theapache64.retrosheet.annotations.Write
import com.squareup.moshi.Types
import java.io.IOException
import java.net.HttpURLConnection
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Invocation

/**
 * Created by theapache64 : Aug 08 Sat,2020 @ 00:09
 */
class GoogleFormHelper(
    private val chain: Interceptor.Chain,
    private val request: Request,
    private val retrosheetInterceptor: RetrosheetInterceptor,
) {

    private val stringMapAdapter by lazy {
        val mapType = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
        retrosheetInterceptor.moshi.adapter<Map<String, String>>(mapType)
    }

    private val listAdapter by lazy {
        val type = Types.newParameterizedType(List::class.java, Object::class.java)
        retrosheetInterceptor.moshi.adapter<List<Any>>(type)
    }

    private val anyAdapter by lazy {
        retrosheetInterceptor.moshi.adapter(Any::class.java)
    }

    companion object {

        private const val FORM_DATA_SPLIT_1 = "FB_PUBLIC_LOAD_DATA_"
        private const val FORM_DATA_SPLIT_2 = "</script>"

        const val SOLUTION_UPDATE = "Please update retrosheet to latest version."

        fun isGoogleFormSubmit(request: Request): Boolean {
            val isForm = (request.tag(Invocation::class.java)?.method()?.getAnnotation(Write::class.java) != null)
            val requestMethod = request.method()
            if (isForm && requestMethod != "POST") {
                throw IllegalArgumentException("@Write should be always @POST, found @$requestMethod")
            }
            return isForm
        }
    }

    fun getFormResponse(): Response {
        val formName = request.url().pathSegments().last()
        val formUrl = retrosheetInterceptor.forms[formName] ?: throw IllegalArgumentException(
            "Couldn't find form with endPoint '$formName'. Are you sure you called 'addSheet('$formName', ...)'"
        )

        val fieldMap = getFieldMapFromUrl(chain, formUrl) ?: throw IllegalArgumentException(
            "Failed to get field map"
        )

        val args = request.tag(Invocation::class.java)!!.arguments()
        if (args.isEmpty()) {
            throw IllegalArgumentException("No argument passed. Param with @Body must be passed")
        }
        val arg = args.first()!!
        val requestJson = anyAdapter.toJson(arg)
        val submitMap = requestJson.run {
            val keyValues = stringMapAdapter.fromJson(this)!!
            val submitMap = mutableMapOf<String, String>()
            for (entry in keyValues.entries) {
                val keyId =
                    fieldMap[entry.key]
                        ?: throw IllegalArgumentException("Couldn't find field '${entry.key}' in the form")
                submitMap["entry.$keyId"] = entry.value
            }
            submitMap
        }

        // Sending post to google forms
        val lastSlashIndex = formUrl.lastIndexOf('/')
        val submitUrl = formUrl.substring(0, lastSlashIndex) + "/formResponse"
        val formBody = FormBody.Builder()
            .apply {
                for ((key, value) in submitMap) {
                    add(key, value)
                }
            }.build()
        val formSubmitRequest = Request.Builder()
            .url(submitUrl)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .method("POST", formBody)
            .build()

        val formResp = chain.proceed(formSubmitRequest)
        if (formResp.code() == HttpURLConnection.HTTP_OK) {
            val respType = MediaType.parse("application/json")
            return formResp.newBuilder()
                .code(HttpURLConnection.HTTP_OK)
                .body(ResponseBody.create(respType, requestJson))
                .build()
        } else {
            val debugData = submitMap.map { it.key + "=" + it.value }.joinToString("&")
            throw IOException("Failed to submit '$formName' with data '$debugData'")
        }
    }

    private fun getFieldMapFromUrl(chain: Interceptor.Chain, formUrl: String): Map<String, String>? {

        val formRequest = Request.Builder()
            .url(formUrl)
            .method("GET", null)
            .build()

        val resp = chain.proceed(formRequest)
        val code = resp.code()
        if (code == HttpURLConnection.HTTP_OK) {
            val htmlBody = resp.body()?.string() ?: throw IOException("Failed to get form data")
            val s1 = htmlBody.split(FORM_DATA_SPLIT_1)
            if (s1.size == 2) {
                val s2 = s1[1].split(FORM_DATA_SPLIT_2)
                if (s2.isNotEmpty()) {
                    val s3 = s2[0]
                    // First square bracket position
                    val fsb = s3.indexOf('[')
                    val lsb = s3.lastIndexOf(']')
                    val pageDataJson = s3.substring(fsb, lsb + 1).trim()
                    val pageData = listAdapter.fromJson(pageDataJson)
                        ?: throw IOException("Failed to decode google form data")
                    val formInfo = pageData[1]
                    if (formInfo is List<*>) {
                        val columns = formInfo[1] ?: throwDataExpectationFailure()
                        if (columns is List<*>) {
                            val fields = mutableMapOf<String, String>()
                            columns.forEach { _column ->
                                val column = _column as List<*>
                                val columnName = column[1] as String
                                // 400
                                val columnIdInDouble = (((column[4] as List<*>)[0] as List<*>)[0]).toString().toDouble()
                                val columnId = String.format("%.0f", columnIdInDouble)

                                if (retrosheetInterceptor.isLoggingEnabled) {
                                    println("Getting form fields")
                                    println("$columnName -> $columnId")
                                }
                                fields[columnName] = columnId
                            }

                            return fields
                        } else {
                            throwDataExpectationFailure()
                        }
                    } else {
                        throwDataExpectationFailure()
                    }
                } else {
                    throwWrongSplit(FORM_DATA_SPLIT_2)
                }
            } else {
                throwWrongSplit(FORM_DATA_SPLIT_1)
            }
        } else {
            throw IOException("Invalid form URL : $formUrl.Got $code ")
        }
        return null
    }

    private fun throwDataExpectationFailure() {
        throw IOException("Data expectation failed. $SOLUTION_UPDATE")
    }

    private fun throwWrongSplit(key: String) {
        throw IllegalArgumentException("Wrong split keyword '$key'. $SOLUTION_UPDATE")
    }
}
