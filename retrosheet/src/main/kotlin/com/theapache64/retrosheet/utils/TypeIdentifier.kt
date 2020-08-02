package com.theapache64.retrosheet.utils

/**
 * Created by theapache64 : Jul 22 Wed,2020 @ 00:04
 */
object TypeIdentifier {
    fun isDouble(field: String): Boolean {
        return try {
            field.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun isInteger(field: String): Boolean {
        return try {
            field.toLong()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun isBoolean(field: String): Boolean {
        return field.equals("true", ignoreCase = true) || field.equals("false", ignoreCase = true)
    }

    fun isNumber(value: String): Boolean {
        return isDouble(value)
    }
}