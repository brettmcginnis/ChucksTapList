package com.serge.chuckstaplist.domain.usecases

sealed interface UseCaseResult<out T> {
    val data: T? get() = null
    val error: Throwable? get() = null

    data object Loading : UseCaseResult<Nothing>
    data class Success<T>(override val data: T) : UseCaseResult<T>
    data class Error(override val error: Throwable) : UseCaseResult<Nothing>
}
