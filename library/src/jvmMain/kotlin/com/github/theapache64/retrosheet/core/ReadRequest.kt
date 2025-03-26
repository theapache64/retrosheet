package com.github.theapache64.retrosheet.core

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.url
import io.ktor.http.content.OutgoingContent


/**
 * To modify request with proper URL
 */
internal fun modRequestForRead(request: HttpRequestBuilder, config: RetrosheetConfig): OutgoingContent? {
    val url = request.url.buildString()
    val pathSegments = request.url.pathSegments
    // Getting docId from URL
    val docId = pathSegments.getOrNull(pathSegments.lastIndex - 1)
        ?: throw IllegalArgumentException("Couldn't find docId from URL '$url'")

    val sheetName = pathSegments.lastOrNull()
        ?: throw IllegalArgumentException("Couldn't find params from URL '$url'. You must specify the page name")

    // Creating realUrl
    val realUrl = UrlBuilder(
        request,
        docId,
        sheetName,
        request.url.parameters.build(),
        config.sheets[sheetName] ?: error("Couldn't find smartQueryMap for pageName '$sheetName'")
    ).build()

    if (config.isLoggingEnabled) {
        val sanitizedUrl = realUrl
            .replace(" ", "%20")
            .replace("*", "%2A")
            .replace("'", "%27")
        println("$TAG : GET --> $sanitizedUrl")
        println("$TAG : GET (html) --> ${sanitizedUrl.replace("tqx=out:csv", "tqx=out:html")}")
    }

    request.url(realUrl)
    request.attributes.put(sheetNameKey, sheetName)
    return null
}