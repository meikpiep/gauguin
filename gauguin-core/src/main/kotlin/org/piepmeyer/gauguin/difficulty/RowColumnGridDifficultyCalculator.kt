package org.piepmeyer.gauguin.difficulty

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.grid.Grid
import java.math.BigInteger
import java.util.function.BiFunction
import kotlin.math.ln

private val logger = KotlinLogging.logger {}

class RowColumnGridDifficultyCalculator(
    private val grid: Grid,
) {
    fun calculate(): Pair<Double, Double> {
        logger.debug { "Calculating row column difficulty of variant ${grid.variant}" }

        val possibleCombinationsByMultiplication = getPossibleCombinations { a, b -> a.multiply(b) }
        val possibleCombinationsByAddition = getPossibleCombinations { a, b -> a.plus(b) }

        val difficultyByPlusAfterMultiplication =
            possibleCombinationsByMultiplication
                .reduce { acc: BigInteger, bigInteger: BigInteger ->
                    acc.plus(bigInteger)
                }
        val difficultyByMultiplicationAfterPlus =
            possibleCombinationsByAddition
                .reduce { acc: BigInteger, bigInteger: BigInteger ->
                    acc.multiply(bigInteger)
                }

        return Pair(
            ln(difficultyByPlusAfterMultiplication.toDouble()) * 10,
            ln(difficultyByMultiplicationAfterPlus.toDouble()),
        )
    }

    private fun getPossibleCombinations(function: BiFunction<BigInteger, BigInteger, BigInteger>): List<BigInteger> {
        val columnToPossibleCombinations = mutableMapOf<Int, BigInteger>()
        val rowToPossibleCombinations = mutableMapOf<Int, BigInteger>()

        grid.cages
            .forEach { cage ->
                val cageCreator = GridSingleCageCreator(grid.variant, cage)

                cage.cells.forEachIndexed { cellIndex, cell ->
                    val possibles =
                        cageCreator.possibleCombinations
                            .map { it[cellIndex] }
                            .distinct()
                            .size
                            .toBigInteger()

                    columnToPossibleCombinations.putIfAbsent(cell.column, 1.toBigInteger())
                    rowToPossibleCombinations.putIfAbsent(cell.row, 1.toBigInteger())

                    columnToPossibleCombinations.put(
                        cell.column,
                        columnToPossibleCombinations.getValue(cell.column).multiply(possibles),
                    )
                    rowToPossibleCombinations.put(cell.row, function.apply(rowToPossibleCombinations.getValue(cell.row), possibles))
                }
            }

        return columnToPossibleCombinations.values + rowToPossibleCombinations.values
    }
}
