package com.swirlfist.simplepixel.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface UseCase<P : UseCaseParams, T> {
    suspend operator fun invoke(params: P) : Result<T>
}

suspend fun <P: UseCaseParams, T> UseCase<P, T>.execute(
    successBlock: (T) -> Unit,
    failureBlock: (Throwable) -> Unit,
    params: P,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
) = withContext(coroutineDispatcher) {
    invoke(params)
}.let { result ->
    result.fold(
        onSuccess = { value -> successBlock(value) },
        onFailure = { error -> failureBlock(error) }
    )
}