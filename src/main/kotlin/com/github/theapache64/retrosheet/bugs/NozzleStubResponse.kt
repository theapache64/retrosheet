package com.github.theapache64.retrosheet.bugs

import com.github.theapache64.retrosheet.RetrosheetInterceptor
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class NozzleStubResponse(
    @Json(name = KEY_NAME) val name: String? = null,
    @Json(name = KEY_TYPE) val type: String? = null,
    @Json(name = KEY_COLOR) val color: String? = null,
    @Json(name = KEY_DEBIT_AT_1_BAR) val debitAt1Bar: Float? = null,
    @Json(name = KEY_DEBIT_AT_2_BAR) val debitAt2Bar: Float? = null,
    @Json(name = KEY_DEBIT_AT_3_BAR) val debitAt3Bar: Float? = null,
    @Json(name = KEY_DEBIT_AT_4_BAR) val debitAt4Bar: Float? = null,
    @Json(name = KEY_DEBIT_AT_5_BAR) val debitAt5Bar: Float? = null,
    @Json(name = KEY_DEBIT_AT_6_BAR) val debitAt6Bar: Float? = null,
    @Json(name = KEY_DEBIT_AT_7_BAR) val debitAt7Bar: Float? = null,
    @Json(name = KEY_DEBIT_AT_8_BAR) val debitAt8Bar: Float? = null
) {

    fun toNozzleStubImpl(): NozzleStubImpl? = name?.let { name ->
        type?.let { type ->
            color?.let { color ->
                NozzleStubImpl(
                    name = name,
                    type = type,
                    color = color,
                    debitAt1Bar = debitAt1Bar ?: DEFAULT_DEBIT,
                    debitAt2Bar = debitAt2Bar ?: DEFAULT_DEBIT,
                    debitAt3Bar = debitAt3Bar ?: DEFAULT_DEBIT,
                    debitAt4Bar = debitAt4Bar ?: DEFAULT_DEBIT,
                    debitAt5Bar = debitAt5Bar ?: DEFAULT_DEBIT,
                    debitAt6Bar = debitAt6Bar ?: DEFAULT_DEBIT,
                    debitAt7Bar = debitAt7Bar ?: DEFAULT_DEBIT,
                    debitAt8Bar = debitAt8Bar ?: DEFAULT_DEBIT
                )
            }
        }
    }

    companion object {
        internal const val SHEET_NAME = "nozzles"
        private const val KEY_NAME = "name"
        private const val KEY_TYPE = "type"
        private const val KEY_COLOR = "color"
        private const val KEY_DEBIT_AT_1_BAR = "debitAt1Bar"
        private const val KEY_DEBIT_AT_2_BAR = "debitAt2Bar"
        private const val KEY_DEBIT_AT_3_BAR = "debitAt3Bar"
        private const val KEY_DEBIT_AT_4_BAR = "debitAt4Bar"
        private const val KEY_DEBIT_AT_5_BAR = "debitAt5Bar"
        private const val KEY_DEBIT_AT_6_BAR = "debitAt6Bar"
        private const val KEY_DEBIT_AT_7_BAR = "debitAt7Bar"
        private const val KEY_DEBIT_AT_8_BAR = "debitAt8Bar"
        private const val DEFAULT_DEBIT = -1f

        internal fun addSheet(interceptorBuilder: RetrosheetInterceptor.Builder) = interceptorBuilder.addSheet(
            SHEET_NAME,
            KEY_NAME,
            KEY_TYPE,
            KEY_COLOR,
            KEY_DEBIT_AT_1_BAR,
            KEY_DEBIT_AT_2_BAR,
            KEY_DEBIT_AT_3_BAR,
            KEY_DEBIT_AT_4_BAR,
            KEY_DEBIT_AT_5_BAR,
            KEY_DEBIT_AT_6_BAR,
            KEY_DEBIT_AT_7_BAR,
            KEY_DEBIT_AT_8_BAR
        )
    }
}