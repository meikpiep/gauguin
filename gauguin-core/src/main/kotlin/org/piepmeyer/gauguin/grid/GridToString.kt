package org.piepmeyer.gauguin.grid

class GridToString(private val grid: Grid) {
    fun printGrid(): String {
        val builder = StringBuilder("Grid:" + System.lineSeparator())

        toStringOfCellValues(builder)
        builder.append(System.lineSeparator())
        builder.append(System.lineSeparator())
        toStringOfCages(builder)

        return builder.toString()
    }

    private fun toStringOfCellValues(builder: StringBuilder) {
        for (cell in grid.cells) {
            val userValue =
                if (cell.userValue == GridCell.NO_VALUE_SET) "-" else cell.userValue.toString()
            val value =
                if (cell.value == GridCell.NO_VALUE_SET) "-" else cell.value.toString()
            builder.append("| ")
                .append(userValue.padStart(2))
                .append(" ")
                .append(value.padStart(2))
                .append(" ")
            if (cell.cellNumber % grid.variant.width == grid.variant.width - 1) {
                builder.append("|")
                builder.append(System.lineSeparator())
            }
        }
    }

    private fun toStringOfCages(builder: StringBuilder) {
        val maximumLength = grid.cells.maxOf { it.displayableUserValueOrPossibles().length }

        for (cell in grid.cells) {
            builder.append("| ")

            val cageText =
                if (cell.cage?.cells?.first() == cell) {
                    cell.cage().cageText()
                } else {
                    ""
                }

            builder.append(cageText.padStart(6))

            builder.append(" ")

            val userValue = cell.displayableUserValueOrPossibles()

            builder.append(cell.cage?.id.toString().padStart(2))
            builder.append(" ")
            builder.append(userValue.padStart(maximumLength))
            builder.append(" ")
            if (cell.cellNumber % grid.variant.width == grid.variant.width - 1) {
                builder.append("|")

                if (cell.cellNumber != grid.cells.size - 1) {
                    builder.append(System.lineSeparator())
                }
            }
        }
    }
}
