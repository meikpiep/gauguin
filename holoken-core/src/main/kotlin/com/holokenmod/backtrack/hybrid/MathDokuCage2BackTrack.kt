package com.holokenmod.backtrack.hybrid

import com.holokenmod.creation.cage.GridSingleCageCreator
import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridCage
import com.holokenmod.grid.GridCell
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class MathDokuCage2BackTrack(
    private val grid: Grid,
    private val isPreSolved: Boolean
) : BackTrackSolutionListener {
    private val logger = LoggerFactory.getLogger(MathDokuCage2BackTrack::class.java)

    private val cages: List<GridCage> = grid.cages
    private val solutions = AtomicInteger(0)
    private var cageCreators: List<GridSingleCageCreator> = mutableListOf()
    private var depthFirstPhase = 0
    private var currentCombination: IntArray = IntArray(1)
    private var sumSolved: Int = 0
    private var threadPool: ExecutorService? = null

    fun solve(): Int {
        cageCreators = cages.map { cage: GridCage ->
            GridSingleCageCreator(grid, cage)
        }.toList()

        depthFirstPhase = if (cages.size > 4) {
            cages.size / 4
        } else {
            cages.size / 2
        }

        currentCombination = IntArray(depthFirstPhase)
        threadPool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            BackTrackThreadFactory(grid, cageCreators, isPreSolved, this)
        )

        try {
            solve(0)
        } catch (_: InterruptedException) {
        }

        logger.debug("Shutdown? " + threadPool!!.isShutdown)
        if (solutions.get() != 2 && !threadPool!!.isShutdown) {
            try {
                threadPool!!.shutdown()
                threadPool!!.awaitTermination(1, TimeUnit.HOURS)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        logger.debug("Solved: " + solutions.get() + " combinations: " + sumSolved)

        return solutions.get()
    }

    @Throws(InterruptedException::class)
    fun solve(cageIndex: Int) {
        if (Thread.currentThread().isInterrupted) {
            throw InterruptedException()
        }
        val cage = cages[cageIndex]
        val cageCreator = cageCreators[cageIndex]
        for (i in cageCreator.possibleNums.indices) {
            val possibleCombination = cageCreator.possibleNums[i]
            val validCells = areCellsValid(cage, possibleCombination)
            if (validCells) {
                currentCombination[cageIndex] = i
                for ((cellNumber, cell) in cage.cells.withIndex()) {
                    cell.setUserValueIntern(possibleCombination[cellNumber])
                }

                // Log.d("backtrack", "Stepping,  " + validCells
                // 		+ " constraints " + cageCreator.satisfiesConstraints(possibleCombination)
                // 		+ System.lineSeparator() + grid.toStringCellsOnly());
                if (cageIndex < depthFirstPhase - 1) {
                    solve(cageIndex + 1)
                } else {
                    // Log.d("backtrack", "Found solution " + grid.toString());
                    if (!threadPool!!.isShutdown) {
                        try {
                            threadPool!!.submit(BackTrackRunnable(currentCombination.clone()))
                        } catch (e: RejectedExecutionException) {
                            return
                        }
                    }
                    sumSolved++
                }
                for (cell in cage.cells) {
                    cell.setUserValueIntern(GridCell.NO_VALUE_SET)
                }
            }
        }

        // Log.d("backtrack", "valid combinations: " + sumSolved);
    }

    private fun areCellsValid(cage: GridCage, possibleCombination: IntArray): Boolean {
        for ((i, cell) in cage.cells.withIndex()) {
            if (grid.isUserValueUsedInSameRow(cell.cellNumber, possibleCombination[i]) ||
                grid.isUserValueUsedInSameColumn(cell.cellNumber, possibleCombination[i])
            ) {
                // 		Log.d("backtrack", "Invalid cell " + cell.getCellNumber()
                // 				+  ", value " + possibleCombination[i]);
                return false
            }
        }
        return true
    }

    @Synchronized
    override fun solutionFound() {
        // Log.i("back2", "Found a solution");
        if (solutions.get() == 2) {
            return
        }
        val currentSolutions = solutions.incrementAndGet()
        if (currentSolutions == 2) {
            // Log.i("back2", "Found 2 solutions");
            threadPool!!.shutdownNow()

            // Thread.currentThread().interrupt();
        }
    }
}
