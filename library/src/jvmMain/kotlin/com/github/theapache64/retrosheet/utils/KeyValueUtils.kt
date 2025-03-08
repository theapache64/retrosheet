package com.github.theapache64.retrosheet.utils

import com.github.theapache64.retrosheet.exception.InvalidKeyValueFormat
import de.siegmar.fastcsv.reader.CsvReader
import de.siegmar.fastcsv.writer.CsvWriter
import de.siegmar.fastcsv.writer.LineDelimiter
import de.siegmar.fastcsv.writer.QuoteStrategy
import java.io.StringWriter

object KeyValueUtils {

    /**
     * To transform given CSV's columns to rows
     *
     * @param responseBody CSV
     * @return transformed CSV
     */
    fun transform(responseBody: String): String {
        val headers = mutableListOf<String>()
        val values = mutableListOf<String>()

        CsvReader.builder()
            .build(responseBody)
            .forEachIndexed { index, row ->
                if (index != 0) {
                    val fieldSize = row.fields.size
                    if (fieldSize != 2) {
                        throw InvalidKeyValueFormat("@KeyValue sheet should have 2 columns. Found $fieldSize @ $row")
                    }

                    headers.add(row.fields[0])
                    values.add(row.fields[1])
                }
            }

        // Building new CSV
        val writer = StringWriter()
        CsvWriter.builder()
            .quoteStrategy(QuoteStrategy.ALWAYS)
            .lineDelimiter(LineDelimiter.LF)
            .build(writer)
            .apply {
                writeRow(*headers.toTypedArray())
                writeRow(*values.toTypedArray())
            }

        return writer.toString().trim()
    }
}
