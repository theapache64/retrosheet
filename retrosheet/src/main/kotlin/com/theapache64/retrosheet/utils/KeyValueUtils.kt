package com.theapache64.retrosheet.utils

import com.theapache64.retrosheet.exception.InvalidKeyValueFormat
import de.siegmar.fastcsv.reader.CsvReader
import de.siegmar.fastcsv.writer.CsvWriter
import java.io.StringWriter

object KeyValueUtils {

    fun transform(responseBody: String): String {
        return CsvReader().apply {
            setContainsHeader(true)
        }.parse(responseBody.reader()).use {

            val headers = mutableListOf<String>()
            val values = mutableListOf<String>()

            while (true) {
                val row = it.nextRow() ?: break
                if (row.fieldCount < 2) {
                    throw InvalidKeyValueFormat("@KeyValue sheet should have 2 columns. Found only ${row.fieldCount} @ $row")
                }
                headers.add(row.getField(0))
                values.add(row.getField(1))
            }

            // Building new CSV
            val writer = StringWriter()
            CsvWriter().apply {
                setAlwaysDelimitText(true)
            }.append(writer).use { appender ->
                appender.appendLine(*headers.toTypedArray())
                appender.appendLine(*values.toTypedArray())
                appender.endLine()
            }
            writer
        }.toString().trim()
    }
}