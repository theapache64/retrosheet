package io.github.theapache64.retrosheet.core

import de.jensklingenberg.ktorfit.annotations
import io.github.theapache64.retrosheet.annotations.Update
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.*
import io.ktor.client.request.url
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.http.headers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.serializer


@OptIn(ExperimentalSerializationApi::class)
internal suspend fun modRequestForAddOrUpdate(
    request: HttpRequestBuilder,
    config: RetrosheetConfig
): OutgoingContent? {
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
    var submitUrl = formUrl.substring(0, lastSlashIndex) + "/formResponse"

    val isUpdate = request.annotations.find { it is Update } != null
    if (isUpdate) {
        // Get the updateKey from params
        val allKeys = request.attributes.allKeys
        var tagValue: String? = null
        for (key in allKeys) {
            val attr = request.attributes.getOrNull(key)
            if (attr is String) {
                tagValue = attr
                break
            }
        }
        if (tagValue == null) {
            throw IllegalArgumentException(
                "updateKey not found in request attributes. " +
                        "Make sure to pass the update key as a String attribute"
            )
        }
        submitUrl = "$submitUrl?edit2=$tagValue"
    }

    request.apply {
        url(submitUrl.proxify(config.useProxyForWrite))
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
