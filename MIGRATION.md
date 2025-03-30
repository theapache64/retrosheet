# Retrosheet Migration Guide: v1 or v2 to v3

> NOTE: This is an AI generated document, reviewed and modified by human, based on the [migration commit diff](https://github.com/theapache64/retrosheet-jvm-sample/commit/475df431575bf5c814b1fd37119fbdedd222c2f1) between v2 and v3. If you prefer concise change list, checkout the diff.

This guide helps you migrate your project from Retrosheet v1 or v2 to the new v3 version. The new version brings several major changes including switching from Retrofit to Ktorfit and from Moshi to Kotlinx Serialization.

## Key Changes

- Replaced Retrofit with Ktorfit
- Switched from Moshi to Kotlinx Serialization
- Updated interceptor configuration to use plugins
- Changed artifact coordinates and version

## Step-by-Step Migration Guide

### 1. Update Gradle Dependencies

Replace your old dependencies with the new ones:

```kotlin
// Old dependencies (v2)
plugins {
    id("com.google.devtools.ksp") version "2.x.x"
    kotlin("jvm") version "2.x.x"
}

dependencies {
    // Moshi
    implementation("com.squareup.moshi:moshi:1.x.x")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.x.x")
    implementation("com.squareup.retrofit2:converter-moshi:2.x.x")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.x.x")

    // Retrosheet
    implementation("com.github.theapache64:retrosheet:2.x.x")
}

// New dependencies (v3)
plugins {
    id("com.google.devtools.ksp") version "2.1.20-1.0.32" // or newer
    kotlin("jvm") version "2.1.20" // or newer
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.20" // Add this
    id("de.jensklingenberg.ktorfit") version "2.4.1" // Add this
}

dependencies {
    // Kotlinx Serialization (replaces Moshi)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    // Ktorfit (replaces Retrofit)
    implementation("de.jensklingenberg.ktorfit:ktorfit-lib:2.4.1")
    implementation("io.ktor:ktor-client-content-negotiation:3.1.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.1.1")

    // Retrosheet v3
    implementation("io.github.theapache64:retrosheet:3.0.0-alpha01") // Note the new artifact coordinates

    // You may need SLF4J No-op implementation
    implementation("org.slf4j:slf4j-nop:2.0.7")
}
```

### 2. Update Data Classes

Replace Moshi annotations with Kotlinx Serialization annotations:

```kotlin
// Old (Moshi)
@JsonClass(generateAdapter = true)
data class Note(
    @Json(name = "Timestamp")
    val createdAt: String? = null,
    @Json(name = "Title")
    val title: String,
    @Json(name = "Description")
    val description: String
)

// New (Kotlinx Serialization)
@Serializable
data class Note(
    @SerialName("Timestamp")
    val createdAt: String? = null,
    @SerialName("Title")
    val title: String,
    @SerialName("Description")
    val description: String?
)
```

### 3. Update API Interfaces

Update imports and method signatures:

```kotlin
// Old (Retrofit)
import com.github.theapache64.retrosheet.annotations.Read
import com.github.theapache64.retrosheet.annotations.Write
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// New (Ktorfit)
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import io.github.theapache64.retrosheet.annotations.Read
import io.github.theapache64.retrosheet.annotations.Write
```

### 4. Update Retrosheet Configuration

Replace the `RetrosheetInterceptor` with the new configuration approach:

```kotlin
// Old (v2)
val retrosheetInterceptor = RetrosheetInterceptor.Builder()
    .setLogging(true)
    // Sheet configuration
    .addSheet(SHEET_NAME, "Timestamp!A:C")
    // Write endpoints
    .addForm(
        ADD_NOTE_ENDPOINT,
        SHEET_NAME,
        mapOf(
            "Title" to "title",
            "Description" to "description"
        )
    )
    .build()

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(retrosheetInterceptor)
    .build()

val moshi = Moshi.Builder().build()

val retrofit = Retrofit.Builder()
    .baseUrl("https://docs.google.com/spreadsheets/d/YOUR_SHEET_ID/")
    .client(okHttpClient)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

return retrofit.create(NotesApi::class.java)

// New (v3)
val config = RetrosheetConfig.Builder()
    .setLogging(true)
    // Sheet configuration
    .addSheet(SHEET_NAME, "Timestamp!A:C")
    // Write endpoints
    .addForm(
        ADD_NOTE_ENDPOINT,
        SHEET_NAME,
        mapOf(
            "Title" to "title",
            "Description" to "description"
        )
    )
    .build()

val ktorClient = HttpClient {
    install(createRetrosheetPlugin(config)) {}
    install(ContentNegotiation) {
        json()
    }
}

val retrofit = Ktorfit.Builder()
    .baseUrl("https://docs.google.com/spreadsheets/d/YOUR_SHEET_ID/")
    .httpClient(ktorClient)
    .converterFactories(RetrosheetConverter(config))
    .build()

return retrofit.createNotesApi() // Note the different method to create API client
```

### 5. Package Changes

Take note of the package name changes:

```kotlin
// Old (v2)
import com.github.theapache64.retrosheet.RetrosheetInterceptor
import com.github.theapache64.retrosheet.annotations.Read
import com.github.theapache64.retrosheet.annotations.Write

// New (v3)
import io.github.theapache64.retrosheet.annotations.Read
import io.github.theapache64.retrosheet.annotations.Write
import io.github.theapache64.retrosheet.core.RetrosheetConfig
import io.github.theapache64.retrosheet.core.RetrosheetConverter
import io.github.theapache64.retrosheet.core.createRetrosheetPlugin
```

## Breaking Changes Summary

1. **Dependency Changes**:
    - Switched from Retrofit to Ktorfit
    - Replaced Moshi with Kotlinx Serialization
    - New artifact coordinates: `io.github.theapache64:retrosheet` (was `com.github.theapache64:retrosheet`)

2. **API Changes**:
    - `RetrosheetInterceptor` → `RetrosheetConfig` and `createRetrosheetPlugin`
    - `retrofit.create()` → `ktorfit.createXXX()`
    - Package naming changes from `com.github.theapache64` to `io.github.theapache64`

3. **Serialization Changes**:
    - `@JsonClass` → `@Serializable`
    - `@Json(name = "")` → `@SerialName("")`

## Common Issues During Migration

1. **Import Errors**: Make sure to update all imports to the new package names.
2. **Serialization Errors**: Ensure all data classes have the `@Serializable` annotation and fields use `@SerialName`.
3. **HTTP Client Configuration**: The Ktor HTTP client configuration is significantly different from OkHttp.
4. **API Method Return Types**: Ktorfit handles suspending functions differently; review your API interface methods.

## Additional Resources

- [Kotlinx Serialization Documentation](https://github.com/Kotlin/kotlinx.serialization)
- [Ktorfit Documentation](https://github.com/Foso/Ktorfit)
- [Retrosheet v3 Sample Project](https://github.com/theapache64/retrosheet-sample)

## Need Help?

If you encounter any issues during migration, please open an issue on the [Retrosheet GitHub repository](https://github.com/theapache64/retrosheet).