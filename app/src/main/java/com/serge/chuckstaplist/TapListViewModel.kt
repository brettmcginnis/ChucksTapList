package com.serge.chuckstaplist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serge.chuckstaplist.TapListViewModel.State.StoreInfo
import com.serge.chuckstaplist.api.ChucksApi
import com.serge.chuckstaplist.api.TapModel
import com.serge.chuckstaplist.foodtruck.FoodTruckEvent
import com.serge.chuckstaplist.foodtruck.FoodTruckRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TapListViewModel @Inject constructor(
    private val api: ChucksApi,
    private val foodTruckRepository: FoodTruckRepository,
) : ViewModel() {

    sealed class State {
        object Empty : State()
        object Loading : State()
        data class Error(val throwable: Throwable) : State()
        data class StoreInfo(val taps: List<TapModel>, val foodTrucks: List<FoodTruckEvent>) : State()
    }

    private val _state = MutableStateFlow<State>(State.Empty)

    val state: StateFlow<State> get() = _state

    fun loadTapList(store: ChucksStore) = with(viewModelScope) {
        _state.value = State.Loading
        coroutineContext.cancelChildren()
        launch {
            _state.value = runCatching {
                val taps = api.getTapList(store.menuStr)
                val foodTrucks = foodTruckRepository.getFoodTrucks(store.calendarId)
                taps to foodTrucks
            }.map { (taps, foodTrucks) -> StoreInfo(taps.filter { it.validEntry }, foodTrucks) }.getOrElse(State::Error)
        }
    }

    private val TapModel.validEntry get() =
        with(name) { any { it.isLetter() } && !startsWith("_") && !startsWith("-") } && price != null
}
