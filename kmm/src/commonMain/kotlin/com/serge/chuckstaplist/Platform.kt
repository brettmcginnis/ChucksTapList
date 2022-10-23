package com.serge.chuckstaplist

import kotlinx.coroutines.CoroutineScope
import org.koin.core.definition.Definition
import org.koin.core.instance.InstanceFactory
import org.koin.core.module.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier

expect abstract class PlatformViewModel() {
    val scope: CoroutineScope
}

expect inline fun <reified T : PlatformViewModel> Module.vm(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>
): Pair<Module, InstanceFactory<T>>
