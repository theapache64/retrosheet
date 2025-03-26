package com.github.theapache64.retrosheetsample.calladapter.either

import java.lang.reflect.Type
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.CallAdapter

class EitherAdapter(
    private val errorType: Type,
    private val successType: Type,
    private val json: Json
) : CallAdapter<Type, EitherCall<Type>> {
    override fun responseType() = successType
    override fun adapt(call: Call<Type>): EitherCall<Type> =
        EitherCall(errorType, call, json)
}
