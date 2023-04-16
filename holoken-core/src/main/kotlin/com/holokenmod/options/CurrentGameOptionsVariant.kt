package com.holokenmod.options

object CurrentGameOptionsVariant {
    @JvmStatic
    fun instance(): GameOptionsVariant {
        return instance
    }

    var instance = GameOptionsVariant.createClassic()
}