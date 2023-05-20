package com.srlee.dlx

internal class DLXNode(val column: DLXColumn, val row: Int) : LL2DNode() {

    init {
        column.up!!.down = this
        up = column.up
        down = column
        column.up = this
        column.incrementSize()
    }
}
