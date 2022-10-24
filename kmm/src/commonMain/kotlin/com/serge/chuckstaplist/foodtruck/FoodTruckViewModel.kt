package com.serge.chuckstaplist.foodtruck

import com.serge.chuckstaplist.ChucksStore
import com.serge.chuckstaplist.PlatformViewModel
import com.serge.chuckstaplist.foodtruck.FoodTruckViewModel.State.TruckList
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FoodTruckViewModel(private val foodTruckRepository: FoodTruckRepository) : PlatformViewModel() {

    sealed class State {
        object Empty : State()
        object Loading : State()
        data class Error(val throwable: Throwable) : State()
        data class TruckList(val store: ChucksStore, val foodTrucks: List<FoodTruckEvent>) : State()
    }

    private val _state = MutableStateFlow<State>(State.Empty)

    val state: StateFlow<State> get() = _state

    fun loadFoodTrucks(store: ChucksStore, force: Boolean = false) = with(scope) {
        if (!force && (state.value as? TruckList)?.store == store) return@with
        _state.value = State.Loading
        coroutineContext.cancelChildren()
        launch {
            _state.value = flow { emit(foodTruckRepository.getFoodTrucks(store.calendarId)) }
                .map<_, State> { TruckList(store, it) }
                .catch { emit(State.Error(it)) }
                .first()
        }
    }
}
