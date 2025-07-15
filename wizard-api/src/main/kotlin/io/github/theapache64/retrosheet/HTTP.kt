package io.github.theapache64.retrosheet

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureHTTP() {
    install(CORS) {
        anyMethod()
        allowHeaders { true }
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }
}
