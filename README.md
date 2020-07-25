# retrosheet 📄
Turn Google Spreadsheet to JSON endpoint. [For Android and JVM].

![](https://i.imgur.com/LYqPOcu.png)

## Benefits

- Since you're using Google's server, you don't have to worry about server health.
- Rapid response and unlimited bandwidth
- Since we're using `OkHttpInterceptor` under the hood, you can easily migrate to your REST API without 
any significant code change.
- You don't have to create an admin panel/dashboard to control the data. You can simply use Google Spreadsheet app.
- You can use this library to create POC/MVP instantly 

## Install 🤝 

```groovy
    implementation 'com.theapache64:retrosheet:1.0.2'
```

## Usage ⌨️ 

### Step 1

- [Create](https://docs.google.com/spreadsheets/u/0/create?usp=sheets_web) a Google spreadsheet and add your data

![data](https://i.imgur.com/3Y114g8.png)

### Step 2

- Press **Share** and copy the link

![copy-link](https://i.imgur.com/MNYD7mg.png)

### Step 3

- Remove contents after the last forward slash from the copied link.

For example, this
```
https://docs.google.com/spreadsheets/d/1IcZTH6-g7cZeht_xr82SHJOuJXD_p55QueMrZcnsAvQ/edit?usp=sharing
```

would become this
```
https://docs.google.com/spreadsheets/d/1IcZTH6-g7cZeht_xr82SHJOuJXD_p55QueMrZcnsAvQ/
```

### Step 4

- Set the `Retrofit` or `OkHttp`'s `baseUrl` with the above link.

![baseUrl](https://i.imgur.com/tFMNEC4.png)


### Step 5

- Add `RetrosheetInterceptor` to your `OkHttpClient`

![interceptor](https://i.imgur.com/5Jrh0Rx.png)


### Step 6

- Create your interface method with `pageName` as endpoint.

![method](https://i.imgur.com/QF8cFVT.png)

`pageName` is your sheet's pageName

![pageName](https://i.imgur.com/qCHDdtI.png)


### Step 7 - Final Step

- Create your response model

![response](https://user-images.githubusercontent.com/9678279/88100193-d7e94a00-cbb9-11ea-9969-9da9f71905aa.png)

Done 👍 Now you can call start calling the API as you'd call normal `Retrofit` or `OkHttp` endpoint
 
## Output 💚

![output](output.gif)

## Advanced

### @Param

![param](https://i.imgur.com/OHyIwHA.png)

You can use `@Param` annotation to query sheet data.

## Samples

- [Nemo](https://github.com/theapache64/nemo) - Android App
- [JVM Sample](https://github.com/theapache64/retrosheet/blob/master/src/main/kotlin/com/theapache64/retrosheet/sample/Main.kt) - CLI App

## TODO ☑️

 - Create an online tool to generate model class from sheet link

## Author ✍️

- theapache64
