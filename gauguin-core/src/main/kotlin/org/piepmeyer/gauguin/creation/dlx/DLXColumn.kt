package org.piepmeyer.gauguin.creation.dlx

internal class DLXColumn : LL2DNode() {
    // Number of items in column
    var size = 0
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
