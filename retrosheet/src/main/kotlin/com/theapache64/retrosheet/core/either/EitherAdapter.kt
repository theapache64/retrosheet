package com.theapache64.retrosheet.core.either

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class EitherAdapter(
    private val type: Type
) : CallAdapter<Type, EitherCall<Type>> {
    override fun responseType() = type
    override fun adapt(call: Call<Type>): EitherCall<Type> =
        EitherCall(call)
}