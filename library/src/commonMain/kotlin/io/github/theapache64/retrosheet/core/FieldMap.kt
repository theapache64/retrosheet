package io.github.theapache64.retrosheet.core

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive


private val ktorClient by lazy { HttpClient() }
private const val FORM_DATA_SPLIT_1 = "FB_PUBLIC_LOAD_DATA_"
private const val FORM_DATA_SPLIT_2 = "</script>"
private const val SOLUTION_UPDATE = "Please update retrosheet to latest version."

internal suspend fun getFieldMapFromUrl(formUrl: String, config: RetrosheetConfig): Map<String, String>? {
    val resp = ktorClient.get(formUrl.proxify(config.useProxyForWrite))
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
                            val columnId = columnIdInDouble.toInt().toString()
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
