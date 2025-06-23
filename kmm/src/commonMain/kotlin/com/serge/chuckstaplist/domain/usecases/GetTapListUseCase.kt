package com.serge.chuckstaplist.domain.usecases

import com.serge.chuckstaplist.ChucksStore
import com.serge.chuckstaplist.api.ChucksApi
import com.serge.chuckstaplist.api.TapModel
import com.serge.chuckstaplist.api.price
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class GetTapListUseCase(
    private val api: ChucksApi
) {
    operator fun invoke(store: ChucksStore): Flow<UseCaseResult<List<TapModel>>> =
        flow { emit(api.getTapList(store.menuStr).filter { it.isValidEntry() }) }
            .map<_, UseCaseResult<List<TapModel>>> { UseCaseResult.Success(it) }
            .onStart{ emit(UseCaseResult.Loading) }
            .catch { emit(UseCaseResult.Error(it)) }
}

private fun TapModel.isValidEntry(): Boolean =
    with(name) { any { it.isLetter() } && !startsWith("_") && !startsWith("-") } && price != null
