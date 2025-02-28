package org.piepmeyer.gauguin.game.save.v1

import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.game.save.SavedCage
import org.piepmeyer.gauguin.game.save.SavedCell
import org.piepmeyer.gauguin.game.save.SavedGrid
import org.piepmeyer.gauguin.game.save.SavedUndoStep
import org.piepmeyer.gauguin.grid.Grid

@Serializable
data class V1SavedGrid(
    val version: Int = 1,
    val variant: V1SavedGameVariant,
    val savedAtInMilliseconds: Long,
    val playTimeInMilliseconds: Long,
    val startedToBePlayed: Boolean,
    val description: String? = null,
    val isActive: Boolean,
    val cells: List<SavedCell>,
    val selectedCellNumber: Int?,
    val invalidCellNumbers: List<Int>,
    val cheatedCellNumbers: List<Int>,
    val cages: List<SavedCage>,
    val undoSteps: List<SavedUndoStep> = emptyList(),
) {
    fun toGrid(): Grid {
        val updatedSavedGrid =
            SavedGrid(
                variant = variant.toUpdatedSavedGameVariant(),
                savedAtInMilliseconds = savedAtInMilliseconds,
                playTimeInMilliseconds = playTimeInMilliseconds,
                startedToBePlayed = startedToBePlayed,
                description = description,
                isActive = isActive,
                cells = cells,
                selectedCellNumber = selectedCellNumber,
                invalidCellNumbers = invalidCellNumbers,
                cheatedCellNumbers = cheatedCellNumbers,
                cages = cages,
                undoSteps = undoSteps,
            )

        return updatedSavedGrid.toGrid()
    }
}
