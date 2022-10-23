package com.serge.chuckstaplist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.definition.Definition
import org.koin.core.instance.InstanceFactory
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier

actual abstract class PlatformViewModel : ViewModel() {
    actual val scope: CoroutineScope = viewModelScope
}

actual inline fun <reified T : PlatformViewModel> Module.vm(
    qualifier: Qualifier?,
    noinline definition: Definition<T>
): Pair<Module, InstanceFactory<T>> = viewModel(qualifier, definition)
