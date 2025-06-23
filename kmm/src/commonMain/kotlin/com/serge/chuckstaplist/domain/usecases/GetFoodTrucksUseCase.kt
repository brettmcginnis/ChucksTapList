package com.serge.chuckstaplist.domain.usecases

import com.serge.chuckstaplist.ChucksStore
import com.serge.chuckstaplist.domain.FoodTruckEvent
import com.serge.chuckstaplist.domain.FoodTruckRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class GetFoodTrucksUseCase(
    private val repository: FoodTruckRepository
) {
    operator fun invoke(store: ChucksStore): Flow<UseCaseResult<List<FoodTruckEvent>>> =
        flow { emit(repository.getFoodTrucks(store.calendarId)) }
            .map<_, UseCaseResult<List<FoodTruckEvent>>> { UseCaseResult.Success(it) }
            .onStart{ emit(UseCaseResult.Loading) }
            .catch { emit(UseCaseResult.Error(it)) }
}
