package org.piepmeyer.gauguin.options

object CurrentGameOptionsVariant {
    @JvmStatic
    fun instance(): GameOptionsVariant {
        return instance
    }

    var instance = GameOptionsVariant.createClassic()
}
