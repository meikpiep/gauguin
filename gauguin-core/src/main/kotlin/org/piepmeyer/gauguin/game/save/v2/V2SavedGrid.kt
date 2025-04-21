package org.piepmeyer.gauguin.game.save.v2

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.game.save.SavedCage
import org.piepmeyer.gauguin.game.save.SavedGameVariant
import org.piepmeyer.gauguin.game.save.SavedGridDifficulty
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCell
import kotlin.time.Duration.Companion.milliseconds

@Serializable
data class V2SavedGrid
    @OptIn(ExperimentalSerializationApi::class)
    constructor(
        @EncodeDefault
        val version: Int = 3,
        val variant: SavedGameVariant,
        val savedAtInMilliseconds: Long,
        val playTimeInMilliseconds: Long,
        val startedToBePlayed: Boolean,
        val description: String? = null,
        val difficulty: SavedGridDifficulty,
        val isActive: Boolean,
        val cells: List<V2SavedCell>,
        val selectedCellNumber: Int?,
        val invalidCellNumbers: List<Int>,
        val cheatedCellNumbers: List<Int>,
        val cages: List<SavedCage>,
        val undoSteps: List<V2SavedUndoStep> = emptyList(),
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
                cell.userValue = if (it.userValue == GridCell.NO_VALUE_SET) null else it.userValue
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
    }
