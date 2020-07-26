package com.theapache64.retrosheet.core.either

import com.theapache64.retrosheet.RetrosheetInterceptor.Companion.ERROR_UNKNOWN
import com.theapache64.retrosheet.utils.MoshiUtils
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EitherCall<T>(
    proxy: Call<T>
) : CallDelegate<T, Either<ApiError, T>>(proxy) {

    companion object {
        val apiErrorJsonAdapter by lazy {
            ApiErrorJsonAdapter(MoshiUtils.moshi)
        }
    }

    override fun enqueueImpl(callback: Callback<Either<ApiError, T>>) = proxy.enqueue(object : Callback<T> {

        override fun onResponse(call: Call<T>, response: Response<T>) {
            val code = response.code()
            val result = if (code in 200 until 300) {
                val body = response.body()!!
                Either.right(body)
            } else {
                val errorJson = response.errorBody()?.string()!!
                val error = apiErrorJsonAdapter.fromJson(errorJson) ?: ApiError(-1, ERROR_UNKNOWN, null)
                Either.left(error)
            }

            callback.onResponse(this@EitherCall, Response.success(result))
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            val result = Either.left(
                ApiError(
                    -1,
                    t.message ?: "Something went wrong",
                    null
                )
            )
            callback.onResponse(this@EitherCall, Response.success(result))
        }
    })

    override fun cloneImpl() = EitherCall(proxy.clone())
    override fun timeout(): Timeout = proxy.timeout()
}