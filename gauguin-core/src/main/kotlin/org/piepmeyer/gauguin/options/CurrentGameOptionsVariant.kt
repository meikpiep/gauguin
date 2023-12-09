package org.piepmeyer.gauguin.options

object CurrentGameOptionsVariant {
    fun instance(): GameOptionsVariant {
        return instance
    }

    var instance = GameOptionsVariant.createClassic()
}
