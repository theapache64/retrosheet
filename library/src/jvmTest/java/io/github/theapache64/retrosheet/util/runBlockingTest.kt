package io.github.theapache64.retrosheet.util

import kotlinx.coroutines.runBlocking

internal fun runBlockingTest(
    invoke: suspend () -> Unit
) = runBlocking {
    invoke()
}