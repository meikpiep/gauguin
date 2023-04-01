package com.srlee.dlx

internal open class LL2DNode {
    var left: LL2DNode?
    var right: LL2DNode?
    var up: LL2DNode?
    var down: LL2DNode? = null

    init {
        up = down
        right = up
        left = right
    }
}