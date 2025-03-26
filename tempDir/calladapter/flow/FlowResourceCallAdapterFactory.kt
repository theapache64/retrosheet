package com.github.theapache64.retrosheetsample.calladapter.flow

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlinx.coroutines.flow.Flow
import retrofit2.CallAdapter
import retrofit2.CallAdapter.Factory
import retrofit2.Retrofit

class FlowResourceCallAdapterFactory(
    private val isSelfExceptionHandling: Boolean = true
) : Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Flow::class.java) {
            return null
        }
        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawObservableType = getRawType(observableType)
        require(rawObservableType == Resource::class.java) { "type must be a resource" }
        require(observableType is ParameterizedType) { "resource must be parameterized" }
        val bodyType = getParameterUpperBound(0, observableType)
        return FlowResourceCallAdapter<Any>(
            bodyType,
            isSelfExceptionHandling
        )
    }
}
