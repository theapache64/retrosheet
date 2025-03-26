package com.github.theapache64.retrosheet.core

import com.github.theapache64.retrosheet.utils.SheetUtils
import kotlinx.serialization.json.Json


class RetrosheetConfig
private constructor(
    val isLoggingEnabled: Boolean = false,
    val sheets: Map<String, Map<String, String>>,
    val forms: Map<String, String>,
    val json: Json
) {

    class Builder {
        private val sheets = mutableMapOf<String, Map<String, String>>()
        private val forms = mutableMapOf<String, String>()
        private var isLoggingEnabled: Boolean = false
        private var json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        fun build(): RetrosheetConfig {
            return RetrosheetConfig(
                isLoggingEnabled,
                sheets,
                forms,
                json
            )
        }

        fun setLogging(isLoggingEnabled: Boolean): Builder {
            this.isLoggingEnabled = isLoggingEnabled
            return this
        }

        @Suppress("unused")
        fun setJson(json: Json): Builder {
            this.json = json
            return this
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun addSheet(sheetName: String, columnMap: Map<String, String>): Builder {
            ColumnNameVerifier(columnMap.keys).verify()
            this.sheets[sheetName] = columnMap
            return this
        }

        /**
         * Columns should be in order
         */
        fun addSheet(sheetName: String, vararg columns: String): Builder {
            return addSheet(
                sheetName,
                SheetUtils.toLetterMap(*columns)
            )
        }

        fun addForm(endPoint: String, formLink: String): Builder {
            if (endPoint.contains('/')) {
                throw java.lang.IllegalArgumentException("Form endPoint name cannot contains '/'. Found '$endPoint'")
            }
            forms[endPoint] = formLink
            return this
        }
    }
}