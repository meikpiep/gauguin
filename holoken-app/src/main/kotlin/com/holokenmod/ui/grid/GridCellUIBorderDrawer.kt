package com.holokenmod.ui.grid

import android.graphics.Canvas
import com.holokenmod.Direction
import com.holokenmod.grid.GridCell

class GridCellUIBorderDrawer(
    private val cellUI: GridCellUI,
    private val paintHolder: GridPaintHolder,
) {
    private val cell: GridCell = cellUI.cell

    fun drawBorders(canvas: Canvas) {
        var north = cellUI.northPixel
        var south = cellUI.southPixel
        var east = cellUI.eastPixel
        var west = cellUI.westPixel

        val cellAbove = cell.hasNeighbor(Direction.NORTH)
        val cellLeft = cell.hasNeighbor(Direction.WEST)
        val cellRight = cell.hasNeighbor(Direction.EAST)
        val cellBelow = cell.hasNeighbor(Direction.SOUTH)

        if (cell.cellBorders.north.isHighlighted) {
            north += 1f
        }
        if (cell.cellBorders.west.isHighlighted) {
            west += 1f
        }
        if (cell.cellBorders.east.isHighlighted) {
            east -= 2f
        }
        if (cell.cellBorders.south.isHighlighted) {
            south -= 2f
        }

        // North
        var borderPaint = paintHolder.getBorderPaint(cell.cellBorders.north)
        if (borderPaint != null) {
            if (!cellAbove && !cellRight) {
                canvas.drawLine(
                    west,
                    north,
                    east - GridUI.CORNER_RADIUS,
                    north,
                    borderPaint
                )
                canvas.drawArc(
                    east - 2 * GridUI.CORNER_RADIUS, north,
                    east, north + 2 * GridUI.CORNER_RADIUS,
                    270f,
                    90f,
                    false,
                    borderPaint
                )
            } else if (!cellAbove && !cellLeft) {
                canvas.drawLine(
                    west + GridUI.CORNER_RADIUS,
                    north,
                    east,
                    north,
                    borderPaint
                )
                canvas.drawArc(
                    west,
                    north,
                    west + 2 * GridUI.CORNER_RADIUS,
                    north + 2 * GridUI.CORNER_RADIUS,
                    180f,
                    90f,
                    false,
                    borderPaint
                )
            } else {
                canvas.drawLine(west, north, east, north, borderPaint)
            }
        }

        // East
        borderPaint = paintHolder.getBorderPaint(cell.cellBorders.east)
        if (borderPaint != null) {
            if (!cellAbove && !cellRight) {
                canvas.drawLine(
                    east,
                    north + GridUI.CORNER_RADIUS,
                    east,
                    south,
                    borderPaint
                )
            } else if (!cellBelow && !cellRight) {
                canvas.drawLine(
                    east,
                    north,
                    east,
                    south - GridUI.CORNER_RADIUS,
                    borderPaint
                )
            } else {
                canvas.drawLine(east, north, east, south, borderPaint)
            }
        }

        // South
        borderPaint = paintHolder.getBorderPaint(cell.cellBorders.south)
        if (borderPaint != null) {
            if (!cellBelow && !cellRight) {
                canvas.drawLine(
                    west,
                    south,
                    east - GridUI.CORNER_RADIUS,
                    south,
                    borderPaint
                )
                canvas.drawArc(
                    east - 2 * GridUI.CORNER_RADIUS,
                    south - 2 * GridUI.CORNER_RADIUS,
                    east,
                    south,
                    0f,
                    90f,
                    false,
                    borderPaint
                )
            } else if (!cellBelow && !cellLeft) {
                canvas.drawLine(
                    west + GridUI.CORNER_RADIUS,
                    south,
                    east,
                    south,
                    borderPaint
                )
                canvas.drawArc(
                    west, south - 2 * GridUI.CORNER_RADIUS,
                    west + 2 * GridUI.CORNER_RADIUS, south,
                    90f,
                    90f,
                    false,
                    borderPaint
                )
            } else {
                canvas.drawLine(west, south, east, south, borderPaint)
            }
        }

        // West
        borderPaint = paintHolder.getBorderPaint(cell.cellBorders.west)
        if (borderPaint != null) {
            if (!cellAbove && !cellLeft) {
                canvas.drawLine(
                    west,
                    north + GridUI.CORNER_RADIUS,
                    west,
                    south,
                    borderPaint
                )
            } else if (!cellBelow && !cellLeft) {
                canvas.drawLine(
                    west,
                    north,
                    west,
                    south - GridUI.CORNER_RADIUS,
                    borderPaint
                )
            } else {
                canvas.drawLine(west, north, west, south, borderPaint)
            }
        }
    }
}