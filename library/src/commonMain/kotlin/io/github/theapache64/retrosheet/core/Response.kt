package io.github.theapache64.retrosheet.core

import io.github.theapache64.retrosheet.annotations.KeyValue
import io.github.theapache64.retrosheet.data.ApiError
import io.github.theapache64.retrosheet.data.SheetError
import io.github.theapache64.retrosheet.utils.CsvConverter
import io.github.theapache64.retrosheet.utils.JsonValidator
import io.github.theapache64.retrosheet.utils.KeyValueUtils
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.annotations
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.TypeData
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.serializer

/**
 * Converts CSV
 */
class RetrosheetConverter(
    private val config: RetrosheetConfig
) : Converter.Factory {

    companion object {
        private val TAG = RetrosheetConverter::class.simpleName
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        return object : Converter.SuspendResponseConverter<HttpResponse, Any> {
            override suspend fun convert(result: KtorfitResult): Any {
                return when (result) {
                    is KtorfitResult.Failure -> {
                        throw result.throwable
                    }
                    is KtorfitResult.Success -> {
                        val response = result.response
                        val request = response.request
                        when {
                            isGoogleFormSubmit(request.annotations, request.method.value) -> {
                                val formResp = result
                                if (formResp.response.status == HttpStatusCode.OK) {
                                    val requestJson = request.attributes[requestJsonKey]
                                    val serializer = serializer(typeData.typeInfo.kotlinType ?: error("Type not found"))
                                    return config.json.decodeFromString(serializer, requestJson)
                                        ?: error("Failed to decode")

                                } else {
                                    val formName = request.attributes[formNameKey]
                                    val submitMap = request.attributes[paramBuilderKey]
                                    val debugData = submitMap
                                        .entries().joinToString("&") { it.key + "=" + it.value.firstOrNull() }
                                    throw IOException("Failed to submit '$formName' with data '$debugData' (code: ${formResp.response.status})")
                                }
                            }

                            isRetrosheetUrl(request.url.toString()) -> {
                                var responseBody = response.bodyAsText()
                                val response: Any

                                // Checking if it's a JSON response. If yes, it's an error else, it's the CSV.
                                val isSpreadsheetError = JsonValidator.isValidJsonObject(responseBody, config.json)
                                if (isSpreadsheetError) {
                                    // It's the spreadsheet error. let's parse it.

                                    // Adding human understandable error
                                    val sheetError = config.json.decodeFromString<SheetError>(responseBody).apply {
                                        this.sheetName = request.attributes[sheetNameKey]
                                        for (error in errors) {
                                            error.humanMessage = translateErrorMessage(
                                                config,
                                                this.sheetName!!, error.detailedMessage
                                            )
                                        }
                                    }

                                    // Converting back to JSON
                                    val apiError = ApiError(
                                        HttpStatusCode.BadRequest.value,
                                        sheetError.errors.firstOrNull()?.humanMessage ?: ERROR_UNKNOWN,
                                        sheetError
                                    )

                                    response = apiError
                                } else {

                                    // It's the CSV.

                                    // Check if it's a KeyValue pair body
                                    val isKeyValue = request.annotations.find { it is KeyValue } != null
                                    if (isKeyValue) {
                                        if (config.isLoggingEnabled) {
                                            println("$TAG : Transforming body to KeyValue")
                                        }
                                        responseBody = KeyValueUtils.transform(responseBody)
                                    }

                                    val type = typeData.typeInfo.kotlinType ?: error("Type not found")
                                    val csvModel = CsvConverter.convertCsvToModel(type, responseBody)
                                    if (csvModel != null) {
                                        if (config.isLoggingEnabled) {
                                            println("$TAG : GET <--- $csvModel")
                                        }
                                        response = csvModel
                                    } else {
                                        // no data
                                        response = ApiError(
                                            HttpStatusCode.NotFound.value,
                                            "No data found",
                                            null
                                        )
                                    }
                                }

                                return response
                            }

                            else -> {
                                result.response.bodyAsText()
                            }
                        }
                    }
                }
            }
        }
    }
}


private fun isReturnTypeList(typeData: TypeData): Boolean {
    val returnType = typeData.typeInfo.kotlinType.toString()
    return returnType.startsWith("java.util.List<") ||
            returnType.startsWith("kotlin.collections.List<") ||
            returnType.startsWith("kotlin.Array<")
}

const val ERROR_UNKNOWN = "Something went wrong"
private const val ERROR_NO_COLUMN_START = "Invalid query: NO_COLUMN"

/**
 * To translate google sheet error message to more understandable form.
 */
private fun translateErrorMessage(
    config: RetrosheetConfig,
    sheetName: String,
    detailedMessage: String
): String {
    var humanMessage = detailedMessage
    if (humanMessage.startsWith(ERROR_NO_COLUMN_START)) {
        // It's a wrong column problem. Now find the column name and
        val errorPart = humanMessage.substring(ERROR_NO_COLUMN_START.length)
        var modErrorPart = errorPart
        config.sheets[sheetName]?.let { table ->
            for (entry in table) {
                if (modErrorPart.contains(entry.value, ignoreCase = true)) {
                    modErrorPart = modErrorPart.replace(entry.value, entry.key)
                    break
                }
            }
        }

        humanMessage = ERROR_NO_COLUMN_START + modErrorPart
    }
    return humanMessage
}