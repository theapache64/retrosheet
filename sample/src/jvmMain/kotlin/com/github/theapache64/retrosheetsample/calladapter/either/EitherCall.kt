package com.github.theapache64.retrosheetsample.calladapter.either

import java.lang.reflect.Type
import kotlinx.serialization.json.Json
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EitherCall<T>(
    private val errorType: Type,
    proxy: Call<T>,
    private val json: Json,
) : CallDelegate<T, Either<*, T>>(proxy) {

    override fun enqueueImpl(callback: Callback<Either<*, T>>) = proxy.enqueue(object : Callback<T> {

        override fun onResponse(call: Call<T>, response: Response<T>) {
            val code = response.code()
            val result = if (code in 200 until 300) {
                val body = response.body()!!
                Either.right(body)
            } else {
                val errorJson = response.errorBody()?.string()!!
                val error = json.decodeFromString<Any>(errorJson)
                Either.left(error)
            }

            callback.onResponse(this@EitherCall, Response.success(result))
        }

        override fun onFailure(call: Call<T>, t: Throwable) {

            val result = Either.left(
                null
            )
            callback.onResponse(this@EitherCall, Response.success(result))
        }
    })

    override fun cloneImpl() = EitherCall<T>(errorType, proxy.clone(), json)
    override fun timeout(): Timeout = proxy.timeout()
}
