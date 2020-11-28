package com.theapache64.retrosheet.utils

import com.squareup.moshi.Types
import de.siegmar.fastcsv.reader.CsvReader

/**
 * Created by theapache64 : Jul 22 Wed,2020 @ 00:05
 */
object CsvConverter {
    fun convertCsvToJson(
        csvData: String,
        isReturnTypeList: Boolean
    ): String? {
        return CsvReader().apply {
            setContainsHeader(true)
        }.parse(csvData.reader()).use {

            // Parsing CSV
            val items = mutableListOf<Map<String, Any?>>().apply {
                // Loading headers first
                while (true) {
                    val row = it.nextRow() ?: break
                    val item = mutableMapOf<String, Any?>().apply {
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
                                    val finalValue = if(field.isNullOrBlank()){
                                        null
                                    }else{
                                        field
                                    }
                                    put(header, finalValue)
                                }
                            }
                        }
                    }
                    add(item)
                }
            }
            when {
                isReturnTypeList -> {
                    val type = Types.newParameterizedType(List::class.java, Map::class.java)
                    val adapter = MoshiUtils.moshi.adapter<List<Map<String, Any?>>>(type)
                    adapter.toJson(items)
                }

                items.isNotEmpty() -> {
                    val type = Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
                    val adapter = MoshiUtils.moshi.adapter<Map<String, Any?>>(type)
                    adapter.toJson(items.first())
                }

                else -> {
                    null
                }
            }
        }
    }


}