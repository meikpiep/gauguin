package com.holokenmod.grid

interface GridView {
    var grid: Grid

    fun requestFocus(): Boolean
    fun invalidate()
}
