package io.github.theapache64.retrosheet

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.json.JSONArray

@Serializable
class CodeResponse(
    @SerialName("gradle") val gradle: String,
    @SerialName("api") val api: String,
    @SerialName("main") val main: String,
)

private val CSV_PARSER_REGEX = "\"([^\"]*)\"(?:,|\$)".toRegex()

private suspend fun ApplicationCall.validateAndGetUrl(): String? {
    val url = request.queryParameters["url"]

    if (url.isNullOrBlank() || !url.matches(Regex("^https://docs\\.google\\.com/forms/d/e/.*$"))) {
        respond(HttpStatusCode.BadRequest, "Invalid or missing URL parameter")
        return null
    }

    return url
}

fun Application.configureRouting() {
    routing {
        // Proxy endpoints for retrosheet
        route("/retrosheet") {
            // OPTIONS endpoint for CORS preflight requests
            options {
                val url = call.validateAndGetUrl() ?: return@options
                call.respond(HttpStatusCode.NoContent)
            }

            // GET endpoint
            get {
                val url = call.validateAndGetUrl() ?: return@get

                try {
                    val response = URL(url).readText()

                    // Forward the response
                    call.respond(HttpStatusCode.OK, response)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Proxy request failed: ${e.message}")
                }
            }

            // POST endpoint
            post {
                val url = call.validateAndGetUrl() ?: return@post

                try {
                    val requestBody = call.receiveText()

                    val urlConnection = URL(url).openConnection() as HttpURLConnection
                    urlConnection.requestMethod = "POST"
                    urlConnection.doOutput = true

                    // Get original content type from request, default to form data
                    val originalContentType = call.request.header("Content-Type") ?: "application/x-www-form-urlencoded"
                    urlConnection.setRequestProperty("Content-Type", originalContentType)

                    // Forward headers
                    call.request.headers.forEach { key, values ->
                        if (key.lowercase() !in listOf("host", "content-length", "content-type", "accept-encoding")) {
                            values.forEach { value ->
                                urlConnection.setRequestProperty(key, value)
                            }
                        }
                    }

                    // Write request body
                    OutputStreamWriter(urlConnection.outputStream).use { writer ->
                        writer.write(requestBody)
                        writer.flush()
                    }

                    val responseCode = urlConnection.responseCode

                    // Read response from appropriate stream based on status code
                    val responseMessage = try {
                        if (responseCode >= 200 && responseCode < 300) {
                            urlConnection.inputStream.bufferedReader().use { it.readText() }
                        } else {
                            urlConnection.errorStream?.bufferedReader()?.use { it.readText() }
                                ?: urlConnection.inputStream.bufferedReader().use { it.readText() }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Error reading response: ${e.message}"
                    }

                    // Forward response content type if available
                    val staticResponseHeaders = listOf<String>(
                        "Content-Type",
                    )

                    for (key in staticResponseHeaders) {
                        urlConnection.getHeaderField(key)?.let { headerValue ->
                            call.response.headers.append(key, headerValue)
                        }
                    }

                    // Forward the response
                    call.respond(HttpStatusCode.fromValue(responseCode), responseMessage)
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, "Proxy request failed: ${e.message}")
                }
            }
        }

        post("/code") {
            // Params: googleSheetUrl, sheetName, googleFormUrl
            val params = call.receiveParameters()

            // Either googleSheetUrl or googleFormUrl is required
            val googleSheetUrl = params["googleSheetUrl"]
            val googleFormUrl = params["googleFormUrl"]
            val sheetName = params["sheetName"]

            if (googleSheetUrl == null && googleFormUrl == null) {
                call.respond(HttpStatusCode.BadRequest, "Either googleSheetUrl or googleFormUrl is required")
                return@post
            }

            // If googleSheetUrl is there, sheetName is required
            if (googleSheetUrl != null && sheetName == null) {
                call.respond(HttpStatusCode.BadRequest, "sheetName is required")
                return@post
            }

            // csvUrl format: https://docs.google.com/spreadsheets/d/12vMK4tdtpEbplmeg3Q3-qc3_yPKO92jp_o41wk4PYHg/gviz/tq?tqx=out:csv&sheet=dc
            val sheetId = googleSheetUrl?.substringAfter("/d/")?.substringBefore("/")
            var sheetHeaders: List<String>? = null
            var formTitles: List<String>? = null
            if (!sheetId.isNullOrEmpty()) {
                val csvUrl = buildString {
                    append("https://docs.google.com/spreadsheets/d/$sheetId/gviz/tq?tqx=out:csv")
                    append("&tq=")
                    append(URLEncoder.encode("SELECT * LIMIT 1", "UTF-8"))
                    append("&sheet=")
                    append(URLEncoder.encode(sheetName, "UTF-8"))
                }

                // GET request
                println("QuickTag: :configureRouting: csvUrl: $csvUrl")
                val csvData = URL(csvUrl).readText().split("\n").getOrNull(0) ?: return@post call.respond(
                    status = HttpStatusCode.InternalServerError, message = "Failed to fetch CSV data from $csvUrl"
                )

                sheetHeaders = CSV_PARSER_REGEX.findAll(csvData)
                    .map {
                        val columnName = it.groupValues[1]
                        if (columnName.equals("Timestamp", ignoreCase = true)) {
                            "\"_$columnName\""
                        } else {
                            "\"$columnName\""
                        }
                    }
                    .toList()
            }



            if (!googleFormUrl.isNullOrBlank()) {
                val googleFormHtmlData = URL(googleFormUrl).readText()
                val formData =
                    googleFormHtmlData.split("FB_PUBLIC_LOAD_DATA_ = ").getOrNull(1)?.split("</script>")?.getOrNull(0)
                        ?.split(";")?.getOrNull(0)

                if (formData != null) {
                    formTitles = JSONArray(formData).getJSONArray(1).getJSONArray(1).map {
                        it as JSONArray
                        "\"${it.get(1) as String}\""
                    }
                }
            }


            val isSeparateModelsNeeded = sheetHeaders != null && formTitles != null && sheetHeaders != formTitles

            val addRowRequestModelName = if (isSeparateModelsNeeded) {
                "AddRowRequest"
            } else {
                "Row"
            }

            val addRowRequestVariableName = if (isSeparateModelsNeeded) {
                "addRowRequest"
            } else {
                "row"
            }

            val addRowRequestModel = if (isSeparateModelsNeeded) {
                """@Serializable
data class $addRowRequestModelName(
${
                    formTitles.joinToString("\n") { fieldName ->
                        """   @SerialName($fieldName)
   val ${fieldName.toCamelcaseVariableName()}: String, """.trimMargin()
                    }
                }
)""".trimIndent()
            } else {
                ""
            }


            val rowModel = """@Serializable
data class Row(
${
                sheetHeaders?.joinToString("\n") { fieldName ->
                    """   @SerialName(${readVarFilter(fieldName)})
   val ${fieldName.toCamelcaseVariableName()}: String, """.trimMargin()
                }
            }
)""".trimIndent()

            val writeSample = if (!formTitles.isNullOrEmpty()) {
                """// Adding sample order
    val newRow = myApi.addRow(
        $addRowRequestModelName(
${
                    formTitles.joinToString("\n") { fieldName ->
                        "           ${fieldName.toCamelcaseVariableName()} = \"sample ${
                            fieldName.replace(
                                "\"",
                                "'"
                            )
                        } input\","
                    }
                }
        )
    )
                    
    println(newRow)
                """.trimIndent()
            } else {
                ""
            }

            val readConfig = if (sheetHeaders != null) {
                """// To Read
        .addSheet(
            "$sheetName", // sheet name
            ${sheetHeaders.joinToString(", ") { fieldName -> "$fieldName" }}  // columns in same order
        )""".trimIndent()
            } else {
                ""
            }

            val addRowKey = "add_row"
            val writeConfig = if (formTitles != null) {
                """// To write
        .addForm(
            "$addRowKey", 
            "$googleFormUrl"
        )""".trimIndent()
            } else {
                ""
            }

            val readSample = if (sheetHeaders != null) {
                """// Reading sample
    val rows = myApi.getRows()
    println(rows)""".trimIndent()
            } else {
                ""
            }


            val readApiFunctions = if (sheetHeaders != null) {
                """@Read("SELECT *")
    @GET("$sheetName")
    suspend fun getRows(): List<Row>""".trimIndent()
            } else {
                ""
            }

            val writeApiFunction = if (formTitles != null) {
                """@Write
    @POST("$addRowKey")
    suspend fun addRow(@Body $addRowRequestVariableName: $addRowRequestModelName): $addRowRequestModelName""".trimIndent()
            } else {
                ""
            }


            call.respond(
                status = HttpStatusCode.OK, message = CodeResponse(
                    gradle = """
                        plugins {
                            kotlin("jvm") version "2.1.10"
                            id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10"
                            id("com.google.devtools.ksp") version "2.1.10-1.0.31"
                            id("de.jensklingenberg.ktorfit") version "2.5.1"
                            ...
                        }
                        ...
                        dependencies {
                            // JSON Serialization
                            implementation("io.ktor:ktor-client-content-negotiation:3.1.3")
                            implementation("io.ktor:ktor-serialization-kotlinx-json:3.1.3")
                            
                            // Ktorfit
                            implementation("de.jensklingenberg.ktorfit:ktorfit-lib:2.5.1")
                            
                            // Retrosheet
                            implementation("io.github.theapache64:retrosheet:3.0.1")
                            
                            ...
                        }
                        ...
                    """.trimIndent(),
                    api = """import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface MyApi {
    $readApiFunctions
    
    $writeApiFunction
    
    // Add more API functions here
}

// Models
$addRowRequestModel

$rowModel""".trimIndent(),
                    main = """suspend fun main() {
    val myApi = createMyApi()
    
    $readSample
    
    $writeSample
}


fun createMyApi(
    configBuilder: RetrosheetConfig.Builder.() -> Unit = {}
): MyApi {
    val config = RetrosheetConfig.Builder()
        .apply { this.configBuilder() }
        .setLogging(true)
        $readConfig
        $writeConfig
        .build()

    val ktorClient = HttpClient {
        install(createRetrosheetPlugin(config)) {}
        install(ContentNegotiation) {
            json()
        }
    }

    val ktorfit = Ktorfit.Builder()
        // GoogleSheet Public URL
        .baseUrl("https://docs.google.com/spreadsheets/d/$sheetId/")
        .httpClient(ktorClient)
        .converterFactories(RetrosheetConverter(config))
        .build()

    return ktorfit.createMyApi()
} """.trimIndent()
                )
            )
        }
    }
}

private fun readVarFilter(column: String): String {
    return if (column.equals("\"_Timestamp\"", ignoreCase = true)) {
        "\"Timestamp\""
    } else {
        column
    }
}

private val variableRegex = Regex("[^a-zA-Z0-9\\s]")

/**
 * string sample: "First Name:" -> firstName
 */
private fun String.toCamelcaseVariableName(): String {
    return replace(variableRegex, " ") // Remove special characters
        .split(Regex("\\s+")) // Split by whitespace
        .filter { it.isNotEmpty() } // Remove empty strings
        .mapIndexed { index, word ->
            if (index == 0) {
                if (!word[0].isLowerCase()) {
                    word.lowercase()
                } else {
                    word
                }
            } else {
                word.lowercase().replaceFirstChar { it.uppercase() }
            }
        }
        .joinToString("")
}
