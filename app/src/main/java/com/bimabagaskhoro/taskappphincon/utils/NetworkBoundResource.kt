package com.bimabagaskhoro.taskappphincon.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.HttpException

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(Resource.Loading(data))
        try {
            saveFetchResult(fetch())
            query().map { Resource.Success(it) }
        } catch (t: HttpException) {
            query().map {
                Resource.Error(shouldFetch(data), t.code(), t.response()?.errorBody())
            }
        }
    } else {
        query().map { Resource.Success(it) }
    }

    emitAll(flow)
}.flowOn(Dispatchers.IO)