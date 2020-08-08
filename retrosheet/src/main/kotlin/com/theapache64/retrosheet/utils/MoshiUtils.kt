package com.theapache64.retrosheet.utils

import com.squareup.moshi.Moshi

/**
 * Created by theapache64 : Jul 25 Sat,2020 @ 22:00
 */
object MoshiUtils {
    val moshi by lazy {
        Moshi.Builder()
            .build()
    }
}