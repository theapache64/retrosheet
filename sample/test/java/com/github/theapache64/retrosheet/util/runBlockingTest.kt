package com.github.theapache64.retrosheet.util

import kotlinx.coroutines.runBlocking

fun runBlockingTest(
    invoke: suspend () -> Unit
) = runBlocking {
    invoke()
}