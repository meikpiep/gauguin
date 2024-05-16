package org.piepmeyer.gauguin.creation

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.RandomSingleton
import org.piepmeyer.gauguin.Randomizer
import org.piepmeyer.gauguin.creation.cage.GridCageOperationDecider
import org.piepmeyer.gauguin.creation.cage.GridCageResultCalculator
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.creation.cage.GridCageTypeLookup
import org.piepmeyer.gauguin.creation.dlx.MathDokuDLXSolver
import org.piepmeyer.gauguin.difficulty.GridDifficultyCalculator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCell
import org.piepmeyer.gauguin.options.GameVariant
import kotlin.math.abs
import kotlin.time.measureTime

private val logger = KotlinLogging.logger {}

class MergingCageGridCalculator(
    private val variant: GameVariant,
    private val randomizer: Randomizer = RandomSingleton.instance,
    private val shuffler: PossibleDigitsShuffler = RandomPossibleDigitsShuffler(),
) : GridCalculator {
    private var singleCageTries = 0
    private var multiCageTries = 0

    override suspend fun calculate(): Grid {
        randomizer.discard()

        val grid = Grid(variant)

        randomiseGrid(grid)

        createSingleCages(grid)

        var runsWithoutSuccess = 0
        var newGrid = grid

        var singleCageMerges = 0

        val mergeWithSingles =
            measureTime {
                while (runsWithoutSuccess < 3) {
                    val (lastSuccess, lastGrid) = mergeSingleCageWithCage(newGrid)

                    if (lastSuccess) {
                        newGrid = lastGrid
                        runsWithoutSuccess = 0
                        singleCageMerges++
                    } else {
                        runsWithoutSuccess++
                    }
                }
            }

        runsWithoutSuccess = 0

        var multiCageMerges = 0

        val mergeNonSingles =
            measureTime {
                while (runsWithoutSuccess < 3) {
                    val (lastSuccess, lastGrid) = mergeCages(newGrid)

                    if (lastSuccess) {
                        newGrid = lastGrid
                        runsWithoutSuccess = 0
                        multiCageMerges++
                    } else {
                        runsWithoutSuccess++
                    }
                }
            }

        val difficulty = GridDifficultyCalculator(newGrid).calculate()

        logger.info {
            "Applied $singleCageMerges single cage merges (tried $singleCageTries in $mergeWithSingles)" +
                ", $multiCageMerges multi cage merges (tried $multiCageTries in $mergeNonSingles)" +
                " and difficulty $difficulty."
        }

        val newDifficulty = GridDifficultyCalculator(newGrid).calculate()
        logger.info { "Difficulty after modification: $newDifficulty" }

        return newGrid
    }

    private suspend fun mergeCages(grid: Grid): Pair<Boolean, Grid> {
        val cages = grid.cages

        cages.shuffled(randomizer.random()).forEach { cage ->
            cages.shuffled(randomizer.random()).forEach { otherCage ->
                if (cage != otherCage && grid.areAdjacent(cage, otherCage) && cage.cells.size + otherCage.cells.size <= 4) {
                    val cellsToBeMerged = cage.cells + otherCage.cells

                    val gridCageType = GridCageTypeLookup(grid, cellsToBeMerged).lookupType()

                    if (gridCageType != null) {
                        val newGrid =
                            createNewGridByMergingTwoCages(grid, cage, otherCage, cellsToBeMerged, gridCageType)

                        if (MathDokuDLXSolver().solve(newGrid) == 1) {
                            return Pair(true, newGrid)
                        }

                        multiCageTries++
                    }
                }
            }
        }

        return Pair(false, grid)
    }

    private suspend fun mergeSingleCageWithCage(grid: Grid): Pair<Boolean, Grid> {
        val singleCages = grid.cages.filter { it.cells.size == 1 }
        val cages = grid.cages

        val singleCageAdjacentCounts =
            singleCages.map { singleCage ->
                grid.cages.count { grid.areAdjacent(singleCage, it) }
            }.distinct().sorted()

        val singleCagesOrdered =
            singleCageAdjacentCounts.map {
                singleCages.filter { singleCage -> grid.cages.count { grid.areAdjacent(singleCage, it) } == it }
                    .shuffled(randomizer.random())
            }.flatten()

        singleCagesOrdered
            .forEach { cage ->
                cages.shuffled(randomizer.random()).forEach { otherCage ->
                    if (cage != otherCage && grid.areAdjacent(cage, otherCage) && cage.cells.size + otherCage.cells.size <= 4) {
                        val cellsToBeMerged = cage.cells + otherCage.cells

                        val gridCageType = GridCageTypeLookup(grid, cellsToBeMerged).lookupType()

                        if (gridCageType != null) {
                            val newGrid =
                                createNewGridByMergingTwoCages(grid, cage, otherCage, cellsToBeMerged, gridCageType)

                            if (MathDokuDLXSolver().solve(newGrid) == 1) {
                                return Pair(true, newGrid)
                            }

                            multiCageTries++
                        }
                    }
                }
            }

        return Pair(false, grid)
    }

    private fun createNewGridByMergingTwoCages(
        grid: Grid,
        cage: GridCage,
        otherCage: GridCage,
        cageCells: List<GridCell>,
        gridCageType: GridCageType? = null,
    ): Grid {
        val newGrid = Grid(variant)

        val oldCages = grid.cages - cage - otherCage

        var cageId = 0

        oldCages.forEach {
            val newCage =
                GridCage.createWithCells(
                    cageId,
                    newGrid,
                    it.action,
                    newGrid.cells.first { newCell ->
                        newCell.cellNumber == it.cells.first().cellNumber
                    },
                    it.cageType,
                )
            cageId++

            newCage.result = it.result

            newGrid.addCage(newCage)
        }

        grid.cells.forEachIndexed { index, gridCell ->
            newGrid.cells[index].value = gridCell.value
        }

        val newCageCells = cageCells.map { oldCell -> newGrid.cells.first { it.cellNumber == oldCell.cellNumber } }

        val operationDecider =
            GridCageOperationDecider(
                randomizer,
                newCageCells,
                variant.options.cageOperation,
            )

        val cageType =
            if (newCageCells.size == 2) {
                if (abs(newCageCells.first().cellNumber - newCageCells.last().cellNumber) == 1) {
                    GridCageType.DOUBLE_HORIZONTAL
                } else {
                    GridCageType.DOUBLE_VERTICAL
                }
            } else {
                gridCageType!!
            }

        val newCage =
            GridCage.createWithCells(
                cageId,
                newGrid,
                operationDecider.decideOperation(),
                newCageCells.minBy { it.cellNumber },
                cageType,
            )

        newGrid.addCage(newCage)

        newCage.result = GridCageResultCalculator(newCage).calculateResultFromAction()

        return newGrid
    }

    private fun createSingleCages(grid: Grid) {
        var cageId = 0

        grid.cells.forEach {
            val cage = GridCage.createWithSingleCellArithmetic(cageId, grid, it)
            cageId++

            grid.addCage(cage)
        }
    }

    private fun randomiseGrid(grid: Grid) {
        val randomizer = GridRandomizer(shuffler, grid)
        randomizer.createGrid()
    }
}