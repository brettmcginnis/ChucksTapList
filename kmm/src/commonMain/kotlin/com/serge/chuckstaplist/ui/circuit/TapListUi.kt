package com.serge.chuckstaplist.ui.circuit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.serge.chuckstaplist.domain.PreferencesRepository
import com.serge.chuckstaplist.platform.ShakeDetector
import com.serge.chuckstaplist.ui.components.LazyTapList
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.ui.Ui
import kotlinx.coroutines.launch

class TapListUi(
    private val preferencesRepository: PreferencesRepository,
    private val shakeDetector: ShakeDetector,
) : Ui<TapListUiState> {

    @Composable
    override fun Content(state: TapListUiState, modifier: Modifier) {
        val coroutineScope = rememberCoroutineScope()
        val tutorialState by rememberRetained { preferencesRepository.shouldShowTutorial }.collectAsState(initial = false)
        LazyTapList(state, tutorialState, shakeDetector, modifier) { showTutorial ->
            coroutineScope.launch { preferencesRepository.setTutorialShown(!showTutorial) }
        }
    }
}

fun tapListUiFactory(
    preferencesRepository: PreferencesRepository,
    shakeDetector: ShakeDetector,
) = Ui.Factory { screen, _ ->
    when (screen) {
        is TapListScreen -> TapListUi(preferencesRepository, shakeDetector)
        else -> null
    }
}
