package com.serge.chuckstaplist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serge.chuckstaplist.TapListViewModel.State.StoreInfo
import com.serge.chuckstaplist.api.ChucksApi
import com.serge.chuckstaplist.api.TapModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TapListViewModel @Inject constructor(private val api: ChucksApi) : ViewModel() {

    sealed class State {
        object Empty : State()
        object Loading : State()
        data class Error(val throwable: Throwable) : State()
        data class StoreInfo(val taps: List<TapModel>) : State()
    }

    private val _state = MutableStateFlow<State>(State.Empty)

    val state: StateFlow<State> get() = _state

    fun loadTapList(store: ChucksStore) = with(viewModelScope) {
        _state.value = State.Loading
        coroutineContext.cancelChildren()
        launch {
            _state.value = flow { emit(api.getTapList(store.menuStr)) }
                .map { taps -> taps.filter { it.validEntry } }
                .map<_, State>(::StoreInfo)
                .catch { emit(State.Error(it)) }
                .first()
        }
    }

    private val TapModel.validEntry get() =
        with(name) { any { it.isLetter() } && !startsWith("_") && !startsWith("-") } && price != null
}
