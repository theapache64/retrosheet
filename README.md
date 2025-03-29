# retrosheet 📄

Turn Google Spreadsheet to JSON endpoint.
Supported Platforms: Android, iOS, JVM and JS

![https://github.com/theapache64/notes](demo.png)

## Benefits 🤗

- 🔄 Migrate to your REST API with minimal code changes.
- 📊 Manage data directly through the Google Spreadsheet app.
- 🏃‍♂️ Speed up development of your POC or MVP with this library.

## Install 🤝

![latestVersion](https://img.shields.io/github/v/release/theapache64/retrosheet)

```kotlin

repositories {
  mavenCentral()
}

dependencies {
  implementation("io.github.theapache64:retrosheet:<latest.version>")
}
```

## Usage ⌨️

### Writing Data ✍️

#### Step 1: Create a Google Form 📝
Create a form with required fields.
![Google Form](https://i.imgur.com/9PeK2EQ.png)

#### Step 2: Set Response Destination 🎯
Choose a Google Sheet to save responses.
![Response Destination](https://i.imgur.com/fIzWiN5.png)
![Sheet Selection](https://i.imgur.com/7ASAB55.png)

#### Step 3: Customize Sheet 📊
Rename sheet and columns (optional).
![Before](https://i.imgur.com/keT8P1o.png)
![After](https://i.imgur.com/N6xfuZK.png)

#### Step 4: Get Form Link 🔗
Press `Send` and copy the link.
![Form Link](https://i.imgur.com/veATAn5.png)

#### Step 5: Create `RetrosheetInterceptor` 🔧
```kotlin
val retrosheetInterceptor = RetrosheetInterceptor.Builder()
    .setLogging(false)
    .addSheet("notes", "created_at", "title", "description")
    .addForm(ADD_NOTE_ENDPOINT, "Form Link")
    .build()

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(retrosheetInterceptor) // and attach the interceptor
    .build()
```

#### Step 6: Create API Interface 🌐
```kotlin
interface NotesApi {
    @Read("SELECT *") 
    @GET("notes")
    suspend fun getNotes(): List<Note>

    @Write
    @POST(ADD_NOTE_ENDPOINT)
    suspend fun addNote(@Body note: Note): Note
}
```


> **@Write** is used for writing data and **@Read**: for reading data

[Query Language Guide](https://developers.google.com/chart/interactive/docs/querylanguage)

### Reading Data 📖

#### Step 7: Share Sheet 🔄
Open a sheet and copy its shareable link.
![Copy Link](https://i.imgur.com/MNYD7mg.png)

### Step 8: Edit Link ✂️
Trim the link after the last '/'.

`https://docs.google.com/spreadsheets/d/1IcZTH6-g7cZeht_xr82SHJOuJXD_p55QueMrZcnsAvQ`~~/edit?usp=sharing~~


### Step 9: Set Base URL 🔗
Use the trimmed link as `baseUrl` in `Retrofit` or `OkHttp`.
![Set Base URL](https://i.imgur.com/tFMNEC4.png)

**Done 👍**

## Full Example 🌟

```kotlin
import com.squareup.moshi.Moshi
import io.github.theapache64.retrosheet.RetrosheetInterceptor
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by theapache64 : Jul 21 Tue,2020 @ 02:11
 */
fun main() = runBlocking {
  
    // Building Retrosheet Interceptor
    val retrosheetInterceptor = RetrosheetInterceptor.Builder()
        .setLogging(false)
        // To Read
        .addSheet(
            "notes", // sheet name
            "created_at", "title", "description" // columns in same order
        )
        // To write
        .addForm(
            "add_note",
            "https://docs.google.com/forms/d/e/1FAIpQLSdmavg6P4eZTmIu-0M7xF_z-qDCHdpGebX8MGL43HSGAXcd3w/viewform?usp=sf_link" // form link
        )
        .build()

    // Building OkHttpClient 
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(retrosheetInterceptor) // and attaching interceptor
        .build()


    val moshi = Moshi.Builder().build()

    // Building retrofit client
    val retrofit = Retrofit.Builder()
        // with baseUrl as sheet's public URL    
        .baseUrl("https://docs.google.com/spreadsheets/d/1YTWKe7_mzuwl7AO1Es1aCtj5S9buh3vKauKCMjx1j_M/") // Sheet's public URL
        // and attach previously created OkHttpClient
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    // Now create the API interface
    val notesApi = retrofit.create(NotesApi::class.java)
  
    // Reading notes
    println(notesApi.getNotes())

    // Adding note
    val addNote = notesApi.addNote(
        Note("Dynamic Note 1", "Dynamic Desc 1")
    )
    println(addNote)
    Unit
}
```

## Samples 🌠

- [Notes - JVM](https://github.com/theapache64/retrosheet/blob/master/src/main/kotlin/com/github/theapache64/retrosheet/sample/notes/Notes.kt)
  - README Example 👆
- [Notes - Android](https://github.com/theapache64/notes) - Android App : Simple note taking app, with add and list
  feature
- [Nemo](https://github.com/theapache64/nemo) - Android App :  E-Commerce App
- [More JVM Samples](https://github.com/theapache64/retrosheet/tree/master/src/main/kotlin/com/github/theapache64/retrosheet/sample)

## Contributing
This project is applying [`ktlint`](https://ktlint.github.io/) (without import ordering since it's conflicted with IDE's 
format). Before creating a PR, please make sure your code is aligned with `ktlint` (`./gradlew ktlint`).
We can run auto-format with:
```shell
./gradlew ktlintFormat
```
## Retrosheet JS

- Coming Soon

## Author ✍️

- theapache64
