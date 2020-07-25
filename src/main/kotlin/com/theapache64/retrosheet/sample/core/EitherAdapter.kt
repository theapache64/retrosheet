package com.theapache64.retrosheet.sample.core

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class EitherAdapter(
    private val type: Type
) : CallAdapter<Type, Call<Either<ApiError, Type>>> {
    override fun responseType() = type
    override fun adapt(call: Call<Type>): Call<Either<ApiError, Type>> = EitherCall(call)
}