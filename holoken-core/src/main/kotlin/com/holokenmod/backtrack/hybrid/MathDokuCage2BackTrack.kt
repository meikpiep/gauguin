package com.holokenmod.backtrack.hybrid

import com.holokenmod.creation.cage.GridSingleCageCreator
import com.holokenmod.grid.*
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors

class MathDokuCage2BackTrack(private val grid: Grid, private val isPreSolved: Boolean) :
    BackTrackSolutionListener {
    private val LOGGER = LoggerFactory.getLogger(
        MathDokuCage2BackTrack::class.java
    )
    private val cages: List<GridCage?>
    private val solutions = AtomicInteger(0)
    private var cageCreators: List<GridSingleCageCreator> = mutableListOf()
    private var DEPTH_FIRST_PHASE = 0
    private var currentCombination: IntArray = IntArray(1) //TODO
    private var sumSolved: Int
    private var threadPool: ExecutorService? = null

    init {
        cages = grid.cages
        sumSolved = 0
    }

    fun solve(): Int {
        cageCreators = cages.parallelStream()
            .map { cage: GridCage? ->
                GridSingleCageCreator(
                    grid, cage!!
                )
            }
            .collect(Collectors.toList())
        for (creator in cageCreators) {
            LOGGER.debug(
                "solving cage "
                        + creator.id
            )
            for (possibleNums in creator.possibleNums) {
                LOGGER.debug("        " + possibleNums.contentToString())
            }
        }
        DEPTH_FIRST_PHASE = if (cages.size > 4) {
            cages.size / 4
        } else {
            cages.size / 2
        }
        currentCombination = IntArray(DEPTH_FIRST_PHASE)
        threadPool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            BackTrackThreadFactory(grid, cageCreators, isPreSolved, this)
        )
        try {
            solve(0)
        } catch (e: InterruptedException) {
        }
        LOGGER.debug("Shutdown? " + threadPool!!.isShutdown)
        if (solutions.get() != 2 && !threadPool!!.isShutdown) {
            try {
                threadPool!!.shutdown()
                threadPool!!.awaitTermination(1, TimeUnit.HOURS)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        LOGGER.debug("Solved: " + solutions.get() + " combinations: " + sumSolved)
        return solutions.get()
    }

    @Throws(InterruptedException::class)
    fun solve(cageIndex: Int) {
        if (Thread.currentThread().isInterrupted) {
            throw InterruptedException()
        }
        val cage = cages[cageIndex]!!
        val cageCreator = cageCreators[cageIndex]
        for (i in cageCreator.possibleNums.indices) {
            val possibleCombination = cageCreator.possibleNums[i]
            val validCells = areCellsValid(cage, possibleCombination)
            if (validCells) {
                currentCombination[cageIndex] = i
                var cellNumber = 0
                for (cell in cage.cells) {
                    cell.setUserValueIntern(possibleCombination[cellNumber])
                    cellNumber++
                }

                //Log.d("backtrack", "Stepping,  " + validCells
                //		+ " constraints " + cageCreator.satisfiesConstraints(possibleCombination)
                //		+ System.lineSeparator() + grid.toStringCellsOnly());
                if (cageIndex < DEPTH_FIRST_PHASE - 1) {
                    solve(cageIndex + 1)
                } else {
                    //Log.d("backtrack", "Found solution " + grid.toString());
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

        //Log.d("backtrack", "valid combinations: " + sumSolved);
    }

    private fun areCellsValid(cage: GridCage, possibleCombination: IntArray): Boolean {
        var i = 0
        for (cell in cage.cells) {
            if (grid.isUserValueUsedInSameRow(cell.cellNumber, possibleCombination[i])
                || grid.isUserValueUsedInSameColumn(cell.cellNumber, possibleCombination[i])
            ) {
                //		Log.d("backtrack", "Invalid cell " + cell.getCellNumber()
                //				+  ", value " + possibleCombination[i]);
                return false
            }
            i++
        }
        return true
    }

    @Synchronized
    override fun solutionFound() {
        //Log.i("back2", "Found a solution");
        if (solutions.get() == 2) {
            return
        }
        val currentSolutions = solutions.incrementAndGet()
        if (currentSolutions == 2) {
            //Log.i("back2", "Found 2 solutions");
            threadPool!!.shutdownNow()

            //Thread.currentThread().interrupt();
        }
    }
}