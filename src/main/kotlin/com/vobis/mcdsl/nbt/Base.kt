package com.vobis.mcdsl.nbt

import java.beans.Introspector

open class NBTBase(
    var invulnerable: Boolean = false,
    var persistenceRequired: Boolean = false,
    var noAI: Boolean = false,
    var silent: Boolean = false,
    var motion: Triple<Int, Int, Int>? = null,
    var direction: Pair<Int, Int>? = null,
    var activeEffects: List<NBTActiveEffect>? = null,
) {
    private fun mapBoolean(bool: Boolean) = if (bool) "1b" else "0b"
}

data class NBTActiveEffect(
    var id: Int,
    var duration: Int = 1,
    var amplifier: Int = 1,
    var ambient: Boolean = true,
    var showParticles: Boolean = true
)

class NBTTag {

}