package com.github.theapache64.retrosheet.core

import de.jensklingenberg.ktorfit.annotations
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin

fun createRetrosheetPlugin(config: RetrosheetConfig): ClientPlugin<Unit> {
    return createClientPlugin("RetrosheetRequestInterceptor") {
        transformRequestBody { request, y, typeInfo ->
            val config: RetrosheetConfig = config
            when {
                isGoogleFormSubmit(request.annotations, request.method.value) -> {
                    modRequestForWrite(
                        request,
                        config
                    )
                }
                isRetrosheetUrl(request.url.toString()) -> modRequestForRead(request, config)
                else -> null
            }
        }
    }
}

