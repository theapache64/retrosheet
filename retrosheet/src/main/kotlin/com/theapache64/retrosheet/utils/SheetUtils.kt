package com.theapache64.retrosheet.utils

/**
 * Created by theapache64 : Aug 01 Sat,2020 @ 10:10
 */
object SheetUtils {
    private const val ALPHABETS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    fun toLetterMap(vararg columns: String): Map<String, String> {
        return columns.mapIndexed { index, columnName ->
            Pair(columnName, getLetterAt(index + 1))
        }.toMap()
    }

    fun getLetterAt(_columnNumber: Int): String {
        var columnNumber = _columnNumber
        // To store result (Excel column name)
        // To store result (Excel column name)
        val columnName = StringBuilder()

        while (columnNumber > 0) {
            val rem: Int = columnNumber % 26
            if (rem == 0) {
                columnName.append("Z")
                columnNumber = columnNumber / 26 - 1
            } else {
                columnName.append((rem - 1 + 'A'.toInt()).toChar())
                columnNumber /= 26
            }
        }

        // Reverse the string and print result

        // Reverse the string and print result
        return columnName.reverse().toString()
    }

}