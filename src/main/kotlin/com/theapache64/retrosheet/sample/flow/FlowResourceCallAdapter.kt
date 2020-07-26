package com.theapache64.retrosheet.sample.flow

import com.theapache64.retrosheet.sample.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.awaitResponse
import java.lang.reflect.Type

/**
 * To convert retrofit response to Flow<Resource<T>>.
 * Inspired from FlowCallAdapterFactory
 */
class FlowResourceCallAdapter<R>(
    private val responseType: Type,
    private val isSelfExceptionHandling: Boolean
) : CallAdapter<R, Flow<Resource<R>>> {

    override fun responseType() = responseType

    @ExperimentalCoroutinesApi
    override fun adapt(call: Call<R>): Flow<Resource<R>> = flow {

        // Firing loading resource
        emit(Resource.Loading<R>())

        val resp = call.awaitResponse()

        if (resp.isSuccessful) {
            resp.body()?.let { data ->
                emit(Resource.Success(null, data))
            } ?: kotlin.run {
                emit(Resource.Error(Throwable("Response can't be null")))
            }
        } else {
            emit(Resource.Error(Throwable(resp.message())))
        }

    }.catch { error ->
        if (isSelfExceptionHandling) {
            emit(Resource.Error(error))
        } else {
            throw error
        }
    }
}