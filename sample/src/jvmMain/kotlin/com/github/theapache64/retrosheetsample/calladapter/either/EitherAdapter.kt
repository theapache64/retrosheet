package com.github.theapache64.retrofit.calladapter.either

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class EitherAdapter(
    private val errorType: Type,
    private val successType: Type
) : CallAdapter<Type, EitherCall<Type>> {
    override fun responseType() = successType
    override fun adapt(call: Call<Type>): EitherCall<Type> =
        EitherCall(errorType, call)
}
