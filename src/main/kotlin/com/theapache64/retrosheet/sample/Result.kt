package com.theapache64.retrosheet.sample

/**
 * Created by theapache64 : Jul 26 Sun,2020 @ 13:22
 */
sealed class Resource<T> {

    class Loading<T> : Resource<T>()

    data class Success<T>(
        val message: String?,
        val data: T
    ) : Resource<T>()

    data class Error<T>(
        val message: Throwable
    ) : Resource<T>()
}
