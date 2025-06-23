package com.serge.chuckstaplist.domain.usecases

import com.serge.chuckstaplist.ChucksStore
import com.serge.chuckstaplist.foodtruck.FoodTruckEvent
import com.serge.chuckstaplist.foodtruck.FoodTruckRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetFoodTrucksUseCase(
    private val repository: FoodTruckRepository
) {
    operator fun invoke(store: ChucksStore): Flow<Result<List<FoodTruckEvent>>> = flow {
        try {
            emit(Result.Loading)
            val foodTrucks = repository.getFoodTrucks(store.calendarId)
            emit(Result.Success(foodTrucks))
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