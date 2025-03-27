package io.github.theapache64.retrosheet.core

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.url
import io.ktor.client.utils.EmptyContent
import io.ktor.http.ParametersBuilder
import io.ktor.http.content.OutgoingContent
import io.ktor.http.headers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.serializer


@OptIn(ExperimentalSerializationApi::class)
internal suspend fun modRequestForWrite(request: HttpRequestBuilder, config: RetrosheetConfig): OutgoingContent {
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
    val serializer = serializer(body::class, emptyList(), false)
    val requestJson = config.json.encodeToString(serializer, body)
    val paramBuilder = requestJson.run {
        val keyValues = config.json.decodeFromString<Map<String, String>>(this)
        val params = ParametersBuilder(size = keyValues.size)
        for (entry in keyValues.entries) {
            val keyId =
                fieldMap[entry.key]
                    ?: throw IllegalArgumentException("Couldn't find field '${entry.key}' in the form")
            params.append("entry.$keyId", entry.value)
        }
        params
    }

    // Sending post to google forms
    val lastSlashIndex = formUrl.lastIndexOf('/')
    val submitUrl = formUrl.substring(0, lastSlashIndex) + "/formResponse"

    request.apply {
        url(submitUrl)
        method = io.ktor.http.HttpMethod.Post
        headers {
            append("Content-Type", "application/x-www-form-urlencoded")
        }
        this.attributes.put(requestJsonKey, requestJson)
        this.attributes.put(paramBuilderKey, paramBuilder)
        this.attributes.put(formNameKey, formName)
    }

    return FormDataContent(paramBuilder.build())
}
