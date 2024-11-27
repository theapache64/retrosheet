package com.github.theapache64.retrosheet.utils

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import de.siegmar.fastcsv.reader.NamedCsvReader

/**
 * Created by theapache64 : Jul 22 Wed,2020 @ 00:05
 */
object CsvConverter {
    fun convertCsvToJson(
        csvData: String,
        isReturnTypeList: Boolean,
        moshi: Moshi
    ): String? {
        val items = mutableListOf<Map<String, Any?>>()

        NamedCsvReader.builder()
            .build(csvData)
            .forEach { row ->
                val item = mutableMapOf<String, Any?>().apply {
                    for ((header, value) in row.fields) {
                        when {
                            TypeIdentifier.isInteger(
                                value
                            ) -> {
                                put(header, value.toLong())
                            }

                            TypeIdentifier.isBoolean(
                                value
                            ) -> {
                                put(header, value.toBoolean())
                            }

                            TypeIdentifier.isDouble(
                                value
                            ) -> {
                                put(header, value.toDouble())
                            }

                            else -> {
                                val finalValue = if (value.isNullOrBlank()) {
                                    null
                                } else {
                                    value
                                }
                                put(header, finalValue)
                            }
                        }
                    }
                }
                items.add(item)
            }

        return when {
            isReturnTypeList -> {
                val type = Types.newParameterizedType(List::class.java, Map::class.java)
                val adapter = moshi.adapter<List<Map<String, Any?>>>(type)
                adapter.toJson(items)
            }

            items.isNotEmpty() -> {
                val type = Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
                val adapter = moshi.adapter<Map<String, Any?>>(type)
                adapter.toJson(items.first())
            }

            else -> {
                null
            }
        }
    }
}
