
import org.koin.core.module.Module
import org.koin.core.module.dsl.binds
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module
import org.piepmeyer.gauguin.difficulty.human2.HumanDifficulty2CalculatorFactory
import org.piepmeyer.gauguin.difficulty.human2.HumanDifficultyCalculatorFactoryImpl

class HumanSolver2Module {
    fun module(): Module =
        module {
            single {
                HumanDifficultyCalculatorFactoryImpl()
            } withOptions {
                binds(listOf(HumanDifficulty2CalculatorFactory::class))
            }
        }
}
