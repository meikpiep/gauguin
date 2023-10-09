package org.piepmeyer.gauguin.grid

interface GridView {
    var grid: Grid

    fun requestFocus(): Boolean
    fun invalidate()
}
