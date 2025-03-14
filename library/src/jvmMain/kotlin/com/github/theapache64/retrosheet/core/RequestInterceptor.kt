package com.github.theapache64.retrosheet.core

import com.github.theapache64.retrosheet.annotations.Write
import com.github.theapache64.retrosheet.utils.SheetUtils
import de.jensklingenberg.ktorfit.annotations
import io.ktor.client.HttpClient
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.EmptyContent
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import io.ktor.util.AttributeKey
import java.io.IOException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.serializer
import sun.jvm.hotspot.debugger.win32.coff.DebugVC50X86RegisterEnums.TAG

val RequestInterceptor = createClientPlugin("RetrosheetRequestInterceptor", ::RequestInterceptorConfig) {
    onRequest { request, content ->
        val config: RequestInterceptorConfig = this@createClientPlugin.pluginConfig
        when {
            isGoogleFormSubmit(request.annotations, request.method.value) -> {
                modRequestForWrite(
                    request,
                    config
                )
            }

            isRetrosheetUrl(request.url.toString()) -> modRequestForRead(request, config)
        }
    }
}

const val SOLUTION_UPDATE = "Please update retrosheet to latest version."

private val URL_REGEX by lazy {
    "https://docs\\.google\\.com/spreadsheets/d/(?<docId>.+)/(?<params>.+)".toRegex()
}

fun isGoogleFormSubmit(
    annotations: List<Any>,
    method: String
): Boolean {
    val isForm = annotations.find { it is Write } != null
    val requestMethod = method
    if (isForm && requestMethod != "POST") {
        throw IllegalArgumentException("@Write should be always @POST, found @$requestMethod")
    }
    return isForm
}


val sheetNameKey = AttributeKey<String>("sheetName")

/**
 * To modify request with proper URL
 */
private fun modRequestForRead(request: HttpRequestBuilder, config: RequestInterceptorConfig) {
    val url = request.url.buildString()
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
        config.sheets[sheetName] ?: error("Couldn't find smartQueryMap for pageName '$sheetName'")
    ).build()

    if (config.isLoggingEnabled) {
        val sanitizedUrl = realUrl.replace(" ", "%20")
        println("$TAG : GET --> $sanitizedUrl")
        println("$TAG : GET (html) --> ${sanitizedUrl.replace("tqx=out:csv", "tqx=out:html")}")
    }

    request {
        url(realUrl)
        attributes.put(sheetNameKey, sheetName)
    }
}

private fun parseSheetName(params: String): String {
    return params.split("?")[0]
}

internal val ktorClient = HttpClient()
private const val FORM_DATA_SPLIT_1 = "FB_PUBLIC_LOAD_DATA_"
private const val FORM_DATA_SPLIT_2 = "</script>"
private const val URL_START = "https://docs.google.com/spreadsheets/d"

fun isRetrosheetUrl(url : String): Boolean {
    return url.startsWith(URL_START)
}

private suspend fun getFieldMapFromUrl(formUrl: String, config: RequestInterceptorConfig): Map<String, String>? {
    val resp = ktorClient.get(formUrl)
    val code = resp.status
    if (code == HttpStatusCode.OK) {
        val htmlBody = resp.bodyAsText()
        val s1 = htmlBody.split(FORM_DATA_SPLIT_1)
        if (s1.size == 2) {
            val s2 = s1[1].split(FORM_DATA_SPLIT_2)
            if (s2.isNotEmpty()) {
                val s3 = s2[0]
                // First square bracket position
                val fsb = s3.indexOf('[')
                val lsb = s3.lastIndexOf(']')
                val pageDataJson = s3.substring(fsb, lsb + 1).trim()
                val pageData = runCatching {
                    config.json.parseToJsonElement(pageDataJson)
                }.getOrElse { error ->
                    throw IOException("Failed to decode google form data: ${error.message}")
                }
                val formInfo = if (pageData is JsonArray) {
                    pageData[1]
                } else {
                    null
                }
                if (formInfo is JsonArray) {
                    val columns = formInfo.getOrNull(1) ?: throwDataExpectationFailure()
                    if (columns is JsonArray) {
                        val fields = mutableMapOf<String, String>()
                        columns.forEach { columnElement ->
                            val column = columnElement as List<*>
                            val columnName = (column[1] as JsonElement).jsonPrimitive.content
                            // 400
                            val columnIdInDouble = (((column[4] as List<*>)[0] as List<*>)[0]).toString().toDouble()
                            val columnId = String.format("%.0f", columnIdInDouble)

                            if (config.isLoggingEnabled) {
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

val requestJsonKey = AttributeKey<String>("requestJson")
val formNameKey = AttributeKey<String>("formName")
val submitMapKey = AttributeKey<Map<String, String>>("submitMap")

private suspend fun modRequestForWrite(request: HttpRequestBuilder, config: RequestInterceptorConfig) {
    val formName = request.url.pathSegments.last()
    val formUrl = config.forms[formName] ?: throw IllegalArgumentException(
        "Couldn't find form with endPoint '$formName'. Are you sure you called 'addSheet('$formName', ...)'"
    )

    val fieldMap = getFieldMapFromUrl(formUrl, config) ?: throw IllegalArgumentException(
        "Failed to get field map"
    )


    val body = request.body
    if (body is EmptyContent) {
        throw IllegalArgumentException("No argument passed. Param with @Body must be passed")
    }
    // Convert arg to a JsonElement first
    val serializer = serializer(body::class.java)
    val requestJson = config.json.encodeToString(serializer, body)
    val submitMap = requestJson.run {
        val keyValues = config.json.decodeFromString<Map<String, String>>(this)
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

    val formBody = formData {
        for ((key, value) in submitMap) {
            append(key, value)
        }
    }

    request {
        url(submitUrl)
        method = io.ktor.http.HttpMethod.Post
        headers {
            append("Content-Type", "application/x-www-form-urlencoded")
        }
        setBody(formBody)
        this.attributes.put(requestJsonKey,requestJson)
        this.attributes.put(submitMapKey,submitMap)
        this.attributes.put(formNameKey, formName)
    }
}


class RequestInterceptorConfig {
    private val _forms = mutableMapOf<String, String>()
    val forms: Map<String, String> = _forms

    private val _sheets = mutableMapOf<String, Map<String, String>>()
    val sheets: Map<String, Map<String, String>> = _sheets

    var json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    var isLoggingEnabled = false


    @Suppress("MemberVisibilityCanBePrivate")
    fun addSheet(sheetName: String, columnMap: Map<String, String>) {
        ColumnNameVerifier(columnMap.keys).verify()
        _sheets[sheetName] = columnMap
    }

    /**
     * Columns should be in order
     */
    fun addSheet(sheetName: String, vararg columns: String) {
        return addSheet(
            sheetName,
            SheetUtils.toLetterMap(*columns)
        )
    }

    fun addForm(endPoint: String, formLink: String) {
        if (endPoint.contains('/')) {
            throw java.lang.IllegalArgumentException("Form endPoint name cannot contains '/'. Found '$endPoint'")
        }
        _forms[endPoint] = formLink
    }
}