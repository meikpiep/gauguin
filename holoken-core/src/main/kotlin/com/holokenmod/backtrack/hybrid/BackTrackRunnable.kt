package com.holokenmod.backtrack.hybrid

import com.holokenmod.creation.cage.GridSingleCageCreator
import com.holokenmod.grid.*
import org.slf4j.LoggerFactory
import java.util.*

class BackTrackRunnable(private val combination: IntArray) : Runnable {
    private val LOGGER = LoggerFactory.getLogger(BackTrackRunnable::class.java)

    private lateinit var grid: Grid
    private lateinit var cageCreators: List<GridSingleCageCreator>
    private var isPreSolved = false
    private lateinit var solutionListener: BackTrackSolutionListener
    private var maxCageIndex = 0

    override fun run() {
        grid = (Thread.currentThread() as BackTrackThread).grid
        maxCageIndex = grid.cages.size - 1
        cageCreators = (Thread.currentThread() as BackTrackThread).cageCreators
        isPreSolved = (Thread.currentThread() as BackTrackThread).isPreSolved
        solutionListener = (Thread.currentThread() as BackTrackThread).solutionListener
        grid.clearUserValues()
        setCombination()
        try {
            solve(combination.size)
        } catch (e: InterruptedException) {
        }
    }

    private fun setCombination() {
        for (cageIndex in combination.indices) {
            val cage = grid.cages[cageIndex]
            val cageCombination = cageCreators[cageIndex].possibleNums[combination[cageIndex]]
            var cellNumber = 0
            for (cell in cage.cells) {
                cell.setUserValueIntern(cageCombination[cellNumber])
                cellNumber++
            }
        }
    }

    @Throws(InterruptedException::class)
    fun solve(cageIndex: Int) {
        if (Thread.currentThread().isInterrupted) {
            throw InterruptedException()
        }
        val cage = grid.cages[cageIndex]
        val cageCreator = cageCreators[cageIndex]
        for (possibleCombination in cageCreator.possibleNums) {
            val validCells = areCellsValid(cage, possibleCombination)
            if (validCells) {
                var cellNumber = 0
                for (cell in cage.cells) {
                    cell.setUserValueIntern(possibleCombination[cellNumber])
                    cellNumber++
                }

                //Log.d("backtrack", "Stepping,  " + validCells
                //		+ " constraints " + cageCreator.satisfiesConstraints(possibleCombination)
                //		+ System.lineSeparator() + grid.toStringCellsOnly());
                if (cageIndex < maxCageIndex) {
                    solve(cageIndex + 1)
                } else {
                    LOGGER.debug("Found solution with " + combination.contentToString() + grid.toString())
                    solutionListener.solutionFound()
                    if (isPreSolved && !grid.isSolved) {
                        solutionListener.solutionFound()
                        return
                    }
                }
                for (cell in cage.cells) {
                    cell.setUserValueIntern(GridCell.NO_VALUE_SET)
                }
            }
        }
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
}