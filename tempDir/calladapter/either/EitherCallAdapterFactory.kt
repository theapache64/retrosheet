package com.github.theapache64.retrosheetsample.calladapter.either

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit

open class EitherCallAdapterFactory(
    private val json : Json
) : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ) = when (getRawType(returnType)) {
        Call::class.java -> {
            val callType = getParameterUpperBound(0, returnType as ParameterizedType)
            when (getRawType(callType)) {
                Either::class.java -> {
                    val errorType = getParameterUpperBound(0, callType as ParameterizedType)
                    val resultType = getParameterUpperBound(1, callType)
                    EitherAdapter(errorType, resultType, json)
                }
                else -> null
            }
        }
        else -> null
    }
}
