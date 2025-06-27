package com.serge.chuckstaplist.ui.circuit

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.serge.chuckstaplist.domain.PreferencesRepository
import com.serge.chuckstaplist.domain.usecases.GetFoodTrucksUseCase
import com.serge.chuckstaplist.domain.usecases.GetTapListUseCase
import com.serge.chuckstaplist.platform.ExternalBrowser
import com.serge.chuckstaplist.platform.ShakeDetector
import com.serge.chuckstaplist.ui.platform.getActivity
import org.koin.core.parameter.parametersOf
import com.serge.chuckstaplist.ui.theme.ChucksTapListTheme
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.animation.AnimatedNavEvent
import com.slack.circuit.foundation.animation.AnimatedNavState
import com.slack.circuit.foundation.animation.AnimatedScreenTransform
import com.slack.circuit.runtime.ExperimentalCircuitApi
import org.koin.compose.koinInject

@OptIn(ExperimentalCircuitApi::class)
@Composable
fun ChucksCircuitApp(
    modifier: Modifier = Modifier
) {
    ChucksTapListTheme {
        val getTapListUseCase: GetTapListUseCase = koinInject()
        val getFoodTrucksUseCase: GetFoodTrucksUseCase = koinInject()
        val activity = getActivity()
        val externalBrowser: ExternalBrowser = koinInject { parametersOf(activity) }
        val shakeDetector: ShakeDetector = koinInject()
        val preferencesRepository: PreferencesRepository = koinInject()
        
        val circuit = Circuit.Builder()
            .addPresenterFactory(storeSelectionPresenterFactory())
            .addPresenterFactory(tapListPresenterFactory(getTapListUseCase, getFoodTrucksUseCase, externalBrowser))
            .addUiFactory(storeSelectionUiFactory())
            .addUiFactory(tapListUiFactory(preferencesRepository, shakeDetector))
            .addAnimatedScreenTransform(StoreSelectionScreen::class, StoreSelectionTransition)
            .addAnimatedScreenTransform(TapListScreen::class, TapListTransition)
            .build()

        val backStack = rememberSaveableBackStack(StoreSelectionScreen)
        CircuitCompositionLocals(circuit) {
            NavigableCircuitContent(
                navigator = rememberCircuitNavigator(backStack) {},
                backStack = backStack,
                modifier = modifier,
            )
        }
    }
}

@OptIn(ExperimentalCircuitApi::class)
data object StoreSelectionTransition : AnimatedScreenTransform {
    override fun AnimatedContentTransitionScope<AnimatedNavState>.enterTransition(animatedNavEvent: AnimatedNavEvent) =
        slideInHorizontally { if(animatedNavEvent == AnimatedNavEvent.Pop) -it else it }

    override fun AnimatedContentTransitionScope<AnimatedNavState>.exitTransition(animatedNavEvent: AnimatedNavEvent) =
        slideOutHorizontally { if(animatedNavEvent == AnimatedNavEvent.Pop) it else -it }
}

@OptIn(ExperimentalCircuitApi::class)
data object TapListTransition : AnimatedScreenTransform {
    override fun AnimatedContentTransitionScope<AnimatedNavState>.enterTransition(animatedNavEvent: AnimatedNavEvent) =
        slideInHorizontally { if (animatedNavEvent == AnimatedNavEvent.Pop) -it else it }

    override fun AnimatedContentTransitionScope<AnimatedNavState>.exitTransition(animatedNavEvent: AnimatedNavEvent) =
        slideOutHorizontally { if (animatedNavEvent == AnimatedNavEvent.GoTo) -it else it }
}
