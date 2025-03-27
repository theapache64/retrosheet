@file:OptIn(ExperimentalSerializationApi::class)

package com.github.theapache64.retrosheet.utils

import app.softwork.serialization.csv.CSVFormat
import kotlin.reflect.KType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.serializer

/**
 * Created by theapache64 : Jul 22 Wed,2020 @ 00:05
 */
internal object CsvConverter {

    fun convertCsvToModel(kType: KType, csvData: String): Any? {
        return CSVFormat.decodeFromString(serializer(kType), csvData)
    }

}
