package com.serge.chuckstaplist

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier

actual abstract class PlatformViewModel {
    actual val scope: CoroutineScope = MainScope()
}

actual inline fun <reified T : PlatformViewModel> Module.vm(
    qualifier: Qualifier?,
    noinline definition: Definition<T>
): KoinDefinition<T> = factory(qualifier, definition)
