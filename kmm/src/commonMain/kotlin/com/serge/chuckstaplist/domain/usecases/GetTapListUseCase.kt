package com.serge.chuckstaplist.domain.usecases

import com.serge.chuckstaplist.ChucksStore
import com.serge.chuckstaplist.api.ChucksApi
import com.serge.chuckstaplist.api.TapModel
import com.serge.chuckstaplist.api.price
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetTapListUseCase(
    private val api: ChucksApi
) {
    operator fun invoke(store: ChucksStore): Flow<Result<List<TapModel>>> = flow {
        try {
            emit(Result.Loading)
            val allTaps = api.getTapList(store.menuStr)
            val validTaps = allTaps.filter { it.isValidEntry() }
            emit(Result.Success(validTaps))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    sealed class Result<out T> {
        object Loading : Result<Nothing>()
        data class Success<T>(val data: T) : Result<T>()
        data class Error(val exception: Exception) : Result<Nothing>()
    }
}

private fun TapModel.isValidEntry(): Boolean =
    with(name) { any { it.isLetter() } && !startsWith("_") && !startsWith("-") } && price != null