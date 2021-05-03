package com.github.theapache64.retrosheet.utils

import com.squareup.moshi.Types
import de.siegmar.fastcsv.reader.CsvParser
import de.siegmar.fastcsv.reader.CsvReader
import de.siegmar.fastcsv.reader.CsvRow

/**
 * Created by theapache64 : Jul 22 Wed,2020 @ 00:05
 */
object CsvConverter {
    private val csvReader: CsvReader = CsvReader().apply { setContainsHeader(true) }

    fun convertCsvToJson(
        csvData: String,
        isReturnTypeList: Boolean
    ): String? {
        val items = csvReader.parse(csvData.reader()).use { csvParser ->
            csvParser
                .asIterator()
                .asSequence()
                .map { row ->
                    csvParser.header.associateWith { key ->
                        val field = row.getField(key)
                        @Suppress("IMPLICIT_CAST_TO_ANY")
                        when {
                            field == null -> null
                            TypeIdentifier.isInteger(field) -> field.toLong()
                            TypeIdentifier.isBoolean(field) -> field.toBoolean()
                            TypeIdentifier.isDouble(field) -> field.toDouble()
                            else -> field.takeUnless { it.isBlank() }
                        }
                    }
                }
                .toList()
        }

        return when {
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

            else -> null
        }
    }

    private fun CsvParser.asIterator(): Iterator<CsvRow> = CsvRowIterator(this)

    private class CsvRowIterator(private val csvParser: CsvParser) : Iterator<CsvRow> {
        private var row = csvParser.nextRow()

        override fun hasNext(): Boolean {
            return row != null
        }

        override fun next(): CsvRow {
            val currentRow = row!!
            row = csvParser.nextRow()
            return currentRow
        }
    }
}
