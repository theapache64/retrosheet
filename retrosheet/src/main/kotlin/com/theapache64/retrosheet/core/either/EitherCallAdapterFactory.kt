package com.theapache64.retrosheet.core.either

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

open class EitherCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ) = when (getRawType(returnType)) {
        Call::class.java -> {
            val callType = getParameterUpperBound(0, returnType as ParameterizedType)
            when (getRawType(callType)) {
                Either::class.java -> {
                    val resultType = getParameterUpperBound(1, callType as ParameterizedType)
                    EitherAdapter(resultType)
                }
                else -> null
            }
        }
        else -> null
    }
}