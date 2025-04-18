package org.piepmeyer.gauguin.game.save

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import kotlin.time.Duration.Companion.milliseconds

@Serializable
data class SavedGrid
    @OptIn(ExperimentalSerializationApi::class)
    constructor(
        @EncodeDefault
        val version: Int = 2,
        val variant: SavedGameVariant,
        val savedAtInMilliseconds: Long,
        val playTimeInMilliseconds: Long,
        val startedToBePlayed: Boolean,
        val description: String? = null,
        val difficulty: SavedGridDifficulty,
        val isActive: Boolean,
        val cells: List<SavedCell>,
        val selectedCellNumber: Int?,
        val invalidCellNumbers: List<Int>,
        val cheatedCellNumbers: List<Int>,
        val cages: List<SavedCage>,
        val undoSteps: List<SavedUndoStep> = emptyList(),
    ) {
        fun toGrid(): Grid {
            val grid = Grid(variant.toVariant(), savedAtInMilliseconds)

            grid.isActive = isActive
            grid.playTime = playTimeInMilliseconds.milliseconds
            grid.startedToBePlayed = startedToBePlayed
            grid.description = description
            grid.difficulty = difficulty.toDifficulty()

            cells.forEach {
                val cell = grid.getCell(it.cellNumber)

                cell.value = it.value
                cell.userValue = it.userValue
                cell.possibles = it.possibles
            }

            selectedCellNumber?.let {
                grid.getCell(it).isSelected = true
            }

            invalidCellNumbers.forEach {
                grid.getCell(it).isInvalidHighlight = true
            }

            cheatedCellNumbers.forEach {
                grid.getCell(it).isCheated = true
            }

            grid.cages =
                cages.map {
                    val cage = GridCage(it.id, grid.options.showOperators, it.action, it.type)

                    cage.result = it.result
                    it.cellNumbers.forEach { cellNumber -> cage.addCell(grid.getCell(cellNumber)) }

                    cage
                }

            grid.undoSteps.addAll(undoSteps.map { it.toUndoStep(grid) })

            return grid
        }

        companion object {
            fun fromGrid(grid: Grid): SavedGrid {
                val savedCells =
                    grid.cells.map {
                        SavedCell.fromCell(it)
                    }
                val savedCages =
                    grid.cages.map {
                        SavedCage.fromCage(it)
                    }
                val savedUndoSteps =
                    grid.undoSteps.map {
                        SavedUndoStep.fromUndoStep(it)
                    }

                return SavedGrid(
                    variant = SavedGameVariant.fromVariant(grid.variant),
                    savedAtInMilliseconds = System.currentTimeMillis(),
                    playTimeInMilliseconds = grid.playTime.inWholeMilliseconds,
                    startedToBePlayed = grid.startedToBePlayed,
                    description = grid.description,
                    difficulty = SavedGridDifficulty.fromDifficulty(grid.difficulty),
                    isActive = grid.isActive,
                    cells = savedCells,
                    selectedCellNumber = grid.selectedCell?.cellNumber,
                    invalidCellNumbers = grid.invalidsHighlighted().map { it.cellNumber },
                    cheatedCellNumbers = grid.cheatedHighlighted().map { it.cellNumber },
                    cages = savedCages,
                    undoSteps = savedUndoSteps,
                )
            }
        }
    }
