package org.piepmeyer.gauguin.creation
import org.koin.core.module.Module
import org.koin.core.module.dsl.binds
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

class GridCreationViaMergeModule {
    fun module(): Module =
        module {
            single {
                MergingCageGridCalculatorFactoryImpl()
            } withOptions {
                binds(listOf(MergingCageGridCalculatorFactory::class))
            }
        }
}
