package com.serge.chuckstaplist

import com.serge.chuckstaplist.TapListViewModel.State.StoreInfo
import com.serge.chuckstaplist.api.ChucksApi
import com.serge.chuckstaplist.api.TapModel
import com.serge.chuckstaplist.api.price
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TapListViewModel(private val api: ChucksApi) : PlatformViewModel() {

    sealed class State {
        object Empty : State()
        object Loading : State()
        data class Error(val throwable: Throwable) : State()
        data class StoreInfo(val store: ChucksStore, val taps: List<TapModel>) : State()
    }

    private val _state = MutableStateFlow<State>(State.Empty)

    val state: StateFlow<State> get() = _state

    fun loadTapList(store: ChucksStore, force: Boolean = false) = with(scope) {
        if (!force && (state.value as? StoreInfo)?.store == store) return@with
        _state.value = State.Loading
        coroutineContext.cancelChildren()
        launch {
            _state.value = flow { emit(api.getTapList(store.menuStr)) }
                .map { taps -> taps.filter { it.validEntry } }
                .map<_, State> { StoreInfo(store, it) }
                .catch { emit(State.Error(it)) }
                .first()
        }
    }

    private val TapModel.validEntry get() =
        with(name) { any { it.isLetter() } && !startsWith("_") && !startsWith("-") } && price != null
}
