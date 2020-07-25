package com.theapache64.retrosheet.utils

import de.siegmar.fastcsv.reader.CsvReader
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by theapache64 : Jul 22 Wed,2020 @ 00:05
 */
object CsvConverter {
    fun convertCsvToJson(csvData: String, newRequest: Request): JSONArray {
        return CsvReader().apply {
            setContainsHeader(true)
        }.parse(csvData.reader()).use {

            // Parsing CSV
            val jaItems = JSONArray().apply {
                // Loading headers first
                while (true) {
                    val row = it.nextRow() ?: break
                    val joItem = JSONObject().apply {
                        for (header in it.header) {
                            val field = row.getField(header)
                            when {
                                TypeIdentifier.isInteger(
                                    field
                                ) -> {
                                    put(header, field.toLong())
                                }

                                TypeIdentifier.isBoolean(
                                    field
                                ) -> {
                                    put(header, field!!.toBoolean())
                                }

                                TypeIdentifier.isDouble(
                                    field
                                ) -> {
                                    put(header, field.toDouble())
                                }

                                else -> {
                                    put(header, field)
                                }
                            }
                        }
                    }
                    put(joItem)
                }
            }
            jaItems
        }
    }
}