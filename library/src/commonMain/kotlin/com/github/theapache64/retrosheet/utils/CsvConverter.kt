@file:OptIn(ExperimentalSerializationApi::class)

package com.github.theapache64.retrosheet.utils

import kotlin.reflect.KType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.serializer

/**
 * Created by theapache64 : Jul 22 Wed,2020 @ 00:05
 */
internal object CsvConverter {

    internal val csv = Csv {
        hasHeaderRecord = true
    }

    fun convertCsvToModel(kType: KType, csvData: String): Any? {
        return csv.decodeFromString(serializer(kType), csvData)
    }

}
