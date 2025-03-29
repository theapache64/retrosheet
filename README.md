# 📝 retrosheet

Turn Google Spreadsheet to JSON endpoint.  
Supported Platforms: Android, iOS, JVM, and JS

![https://github.com/theapache64/notes](demo.png)

## 🤝 Benefits

- 🔄 Migrate to your REST API with minimal code changes.
- 📊 Manage data directly through the Google Spreadsheet app.
- 🏃‍♂️ Speed up development of your POC or MVP with this library.

## 🤝 Install

![latestVersion](https://img.shields.io/github/v/release/theapache64/retrosheet)

```kotlin
repositories {
  mavenCentral()
}

dependencies {
  implementation("io.github.theapache64:retrosheet:<latest.version>")
}
```

## ⌘️ Usage

### ✍️ Writing Data

#### 📝 Step 1: Create a Google Form
Create a form with required fields.  
![Google Form](https://i.imgur.com/9PeK2EQ.png)

#### 🎯 Step 2: Set Response Destination
Choose a Google Sheet to save responses.  
![Response Destination](https://i.imgur.com/fIzWiN5.png)  
![Sheet Selection](https://i.imgur.com/7ASAB55.png)

#### 📊 Step 3: Customize Sheet
Rename sheet and columns (optional).  
![Before](https://i.imgur.com/keT8P1o.png)  
![After](https://i.imgur.com/N6xfuZK.png)

#### 🔗 Step 4: Get Form Link
Press `Send` and copy the link.  
![Form Link](https://i.imgur.com/veATAn5.png)

#### 🔧 Step 5: Create `RetrosheetConfig` and attach it to the client
```kotlin
val config = RetrosheetConfig.Builder()
  .setLogging(true)
  // For reading from sheet
  .addSheet(
    "notes", // sheet name
    "created_at", "title", "description" // columns in same order
  )
  // For writing to sheet
  .addForm(
    "add_note",
    "https://docs.google.com/forms/d/e/1FAIpQLSdmavg6P4eZTmIu-0M7xF_z-qDCHdpGebX8MGL43HSGAXcd3w/viewform?usp=sf_link" // form link
  )
  .build()

val ktorClient = HttpClient {
  install(createRetrosheetPlugin(config)) {}
  ...
}
```

#### 🌐 Step 6: Create API Interface
```kotlin
interface NotesApi {
    @Read("SELECT *")
    @GET("notes")
    suspend fun getNotes(): List<Note>

    @Write
    @POST("add_note")
    suspend fun addNote(@Body note: Note): Note
}
```

> **@Write** is used for writing data and **@Read** for reading data.

[Query Language Guide](https://developers.google.com/chart/interactive/docs/querylanguage)

### 📚 Reading Data

#### 🔄 Step 7: Share Sheet
Open a sheet and copy its shareable link.  
![Copy Link](https://i.imgur.com/MNYD7mg.png)

#### ✂️ Step 8: Edit Link
Trim the link after the last '/'.

`https://docs.google.com/spreadsheets/d/1IcZTH6-g7cZeht_xr82SHJOuJXD_p55QueMrZcnsAvQ`~~/edit?usp=sharing~~

#### 🔗 Step 9: Set Base URL
Use the trimmed link as `baseUrl` in `Ktorfit`.

```kotlin
val retrofit = Ktorfit.Builder()
         // Like this 👇🏼
        .baseUrl("https://docs.google.com/spreadsheets/d/1YTWKe7_mzuwl7AO1Es1aCtj5S9buh3vKauKCMjx1j_M/")
        .httpClient(ktorClient)
        .converterFactories(RetrosheetConverter(config))
        .build()
```

**Done 👍**

## 🌠 Full Example

```kotlin
suspend fun main() {
  val notesApi = buildNotesApi()
  println(notesApi.getNotes())

  // Adding sample order
  val newNote = notesApi.addNote(
    Note(
      createdAt = null,
      title = "Dynamic Note 1",
      description = "Dynámic Desc 1: ${Date()}"
    )
  )

  println(newNote)
}


fun createNotesApi(
  configBuilder: RetrosheetConfig.Builder.() -> Unit = {}
): NotesApi {
  val config = RetrosheetConfig.Builder()
    .apply { this.configBuilder() }
    .setLogging(true)
    // To Read
    .addSheet(
      "notes", // sheet name
      "created_at", "title", "description" // columns in same order
    )
    // To write
    .addForm(
      "add_note",
      // Google form name
      "https://docs.google.com/forms/d/e/1FAIpQLSdmavg6P4eZTmIu-0M7xF_z-qDCHdpGebX8MGL43HSGAXcd3w/viewform?usp=sf_link"
    )
    .build()

  val ktorClient = HttpClient {
    install(createRetrosheetPlugin(config)) {}
    install(ContentNegotiation) {
      json()
    }
  }

  val retrofit = Ktorfit.Builder()
    // GoogleSheet Public URL
    .baseUrl("https://docs.google.com/spreadsheets/d/1YTWKe7_mzuwl7AO1Es1aCtj5S9buh3vKauKCMjx1j_M/")
    .httpClient(ktorClient)
    .converterFactories(RetrosheetConverter(config))
    .build()

  return retrofit.createNotesApi()
}
```
- Source: https://github.com/theapache64/retrosheet-jvm-sample. Check `sample` directory for more samples

## 🔄 Migration
- Want to migrate from 1.x.x or 2.x.x? [Here's](https://github.com/theapache64/retrosheet-jvm-sample/commit/475df431575bf5c814b1fd37119fbdedd222c2f1) how you can do it

## 🤝 Contributing
This project applies [`ktlint`](https://ktlint.github.io/) (without import ordering since it's conflicted with IDE's format). Before creating a PR, please make sure your code is aligned with `ktlint` (`./gradlew ktlint`).

We can run auto-format with:
```shell
./gradlew ktlintFormat
```

## ✍️ Author
- theapache64  

