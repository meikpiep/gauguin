package org.piepmeyer.gauguin.creation.dlx

internal class DLXColumn : LL2DNode() {
    var size = // Number of items in column
        0
            private set

    init {
        up = this
        down = this
    }

    fun decrementSize() {
        size--
    }

    fun incrementSize() {
        size++
    }
}
