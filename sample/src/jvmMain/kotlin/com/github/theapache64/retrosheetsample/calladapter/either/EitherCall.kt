package com.github.theapache64.retrosheetsample.calladapter.either

import com.github.theapache64.retrofit.calladapter.either.CallDelegate
import com.github.theapache64.retrofit.calladapter.either.Either
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class EitherCall<T>(
    private val errorType: Type,
    proxy: Call<T>
) : CallDelegate<T, Either<*, T>>(proxy) {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    override fun enqueueImpl(callback: Callback<Either<*, T>>) = proxy.enqueue(object : Callback<T> {

        override fun onResponse(call: Call<T>, response: Response<T>) {
            val code = response.code()
            val result = if (code in 200 until 300) {
                val body = response.body()!!
                Either.right(body)
            } else {
                val errorJson = response.errorBody()?.string()!!
                val error = moshi.adapter<Any>(errorType)
                    .fromJson(errorJson)
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

    override fun cloneImpl() = EitherCall<T>(errorType, proxy.clone())
    override fun timeout(): Timeout = proxy.timeout()
}
