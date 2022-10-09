# retrosheet 📄

Turn Google Spreadsheet to JSON endpoint. [For Android and JVM].

![https://github.com/theapache64/notes](demo.png)

## Benefits 🤗

- No worries about server health (because you're using Google's server 😋)
- Rapid response and unlimited bandwidth
- You can easily migrate to your REST API without any significant code change.
- You don't have to create an admin panel/dashboard to control the data. You can simply use Google Spreadsheet app (web/mobile).
- You can use this library to create POC/MVP instantly

## Install 🤝

![latestVersion](https://img.shields.io/github/v/release/theapache64/retrosheet)

```groovy

repositories {
  maven { url 'https://jitpack.io' } // Add jitpack
}

dependencies {
  implementation 'com.github.theapache64:retrosheet:latest.version'
}
```
## Usage ⌨️

### How to write data ? ✍️

#### Step 1 : Writing Data To Sheet

- [Create a Google Form](https://docs.google.com/forms/u/0/) with some fields
  ![](https://i.imgur.com/9PeK2EQ.png)

#### Step 2

- Select response destination and select/create a Google sheet to store the responses.
  ![](https://i.imgur.com/fIzWiN5.png)
  ![](https://i.imgur.com/7ASAB55.png)

#### Step 3

- Now you can open the sheet and change sheet name and column names if you want. This is just to make the Google sheet
  table look like a real database table (optional)

I've changed
![](https://i.imgur.com/keT8P1o.png)
to
![](https://i.imgur.com/N6xfuZK.png)

#### Step 4

- Next, Press the `Send` button and copy the form link

![](https://i.imgur.com/veATAn5.png)

#### Step 5

- Now let's go to our code and create our `RetrosheetInterceptor`

```kotlin
val retrosheetInterceptor = RetrosheetInterceptor.Builder()
    .setLogging(false)
    // To Read
    .addSheet(
        "notes", // sheet name
        "created_at", "title", "description" // columns in same order
    )
    // To write
    .addForm(
        ADD_NOTE_ENDPOINT,
        "https://docs.google.com/forms/d/e/1FAIpQLSdmavg6P4eZTmIu-0M7xF_z-qDCHdpGebX8MGL43HSGAXcd3w/viewform?usp=sf_link" // form link
    )
    .build()
```

#### Step 6

- Next, let's create a normal Retrofit API interface

```kotlin
interface NotesApi {

    @Read("SELECT *") 
    @GET("notes") // sheet name
    suspend fun getNotes(): List<Note>

    @Write
    @POST(ADD_NOTE_ENDPOINT) // form name
    suspend fun addNote(@Body addNoteRequest: AddNoteRequest): AddNoteRequest
}
```

- **@Write** : To write data to a sheet

- **@Read** : To read data from a sheet.

You can lean more about query language from here : https://developers.google.com/chart/interactive/docs/querylanguage.

**NOTE**: You can use your column name in the query rather than using column letter such as `A,B,C` etc.

### How to read data ? 📖

#### Step 7 : Reading data from Sheet

- We're done configuring the writing part. Now let's finish the reading part. Create/open a google sheet, (it can be
  either form connected, or a simple Google sheet).

- Press **Share** and copy the link

![copy-link](https://i.imgur.com/MNYD7mg.png)

### Step 8

- Remove contents after the last forward slash from the copied link.

For example, this

```
https://docs.google.com/spreadsheets/d/1IcZTH6-g7cZeht_xr82SHJOuJXD_p55QueMrZcnsAvQ/edit?usp=sharing
```

would become this

```
https://docs.google.com/spreadsheets/d/1IcZTH6-g7cZeht_xr82SHJOuJXD_p55QueMrZcnsAvQ/
```

### Step 9

- Finally, Set the `Retrofit` or `OkHttp`'s `baseUrl` with the above link.

![baseUrl](https://i.imgur.com/tFMNEC4.png)

Done 👍

## Full Example 🌟

```kotlin
import com.squareup.moshi.Moshi
import com.github.theapache64.retrosheet.RetrosheetInterceptor
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
        AddNoteRequest("Dynamic Note 1", "Dynamic Desc 1")
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
