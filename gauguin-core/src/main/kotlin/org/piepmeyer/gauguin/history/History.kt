package org.piepmeyer.gauguin.history

import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.difficulty.ensureDifficultyCalculated
import org.piepmeyer.gauguin.game.save.SavedGameOptionsVariant
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import kotlin.time.Clock
import kotlin.time.Duration

class History(
    override val events: List<HistoryEvent>,
) : HistoryView(events) {
    fun view(size: GridSize): HistoryView {
        val filteredEvents = events.filter { it.gridInfo.size == size }

        return HistoryView(filteredEvents)
    }

    fun gridSizes(): Set<GridSize> = events.map { it.gridInfo.size }.toSet()
}

@Serializable
sealed class HistoryEvent(
    open val gridInfo: GridHistoryInfo,
) {
    class GridSolved(
        override val gridInfo: GridHistoryInfo,
    ) : HistoryEvent(gridInfo)

    class GridUnsolved(
        override val gridInfo: GridHistoryInfo,
    ) : HistoryEvent(gridInfo)
}

@Serializable
data class GridHistoryInfo(
    val startedAt: Long,
    val endedAt: Long,
    val size: GridSize,
    val optionsVariant: SavedGameOptionsVariant,
    val classicDifficulty: Double,
    val duration: Duration,
) {
    companion object {
        fun of(grid: Grid): GridHistoryInfo =
            GridHistoryInfo(
                startedAt = grid.creationDate,
                endedAt = Clock.System.now().epochSeconds,
                size = grid.gridSize,
                optionsVariant = SavedGameOptionsVariant.fromOptionsVariant(grid.options),
                classicDifficulty = grid.ensureDifficultyCalculated(),
                duration = grid.playTime,
            )
    }
}
