package com.theapache64.retrosheet.sample.core

import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EitherCall<T>(proxy: Call<T>) : CallDelegate<T, Either<ApiError, T>>(proxy) {

    override fun enqueueImpl(callback: Callback<Either<ApiError, T>>) = proxy.enqueue(object : Callback<T> {

        override fun onResponse(call: Call<T>, response: Response<T>) {
            val code = response.code()
            val result = if (code in 200 until 300) {
                val body = response.body()!!
                Either.right(body)
            } else {
                val message = response.errorBody()?.string() ?: "Something went wrong"
                Either.left(ApiError(code, message))
            }

            callback.onResponse(this@EitherCall, Response.success(result))
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            val result = Either.left(ApiError(-1, t.message ?: "Something went wrong"))
            callback.onResponse(this@EitherCall, Response.success(result))
        }
    })

    override fun cloneImpl() = EitherCall(proxy.clone())
    override fun timeout(): Timeout = proxy.timeout()
}