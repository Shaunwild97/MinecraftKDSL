package com.vobis.mcdsl.nbt

import com.fasterxml.jackson.annotation.JsonProperty

class NBTVillager(
    invulnerable: Boolean = false,
    persistenceRequired: Boolean = false,
    noAI: Boolean = false,
    silent: Boolean = false,
    motion: Triple<Int, Int, Int>? = null,
    direction: Pair<Int, Int>? = null,
    activeEffects: List<NBTActiveEffect>? = null,

    var villagerData: VillagerData? = null,
    var offers: Offers? = null,
) : NBTBase(invulnerable, persistenceRequired, noAI, silent, motion, direction, activeEffects) {
    data class VillagerData(
        var profession: String,
        var level: Int = 1,
        var type: String = "plains"
    )

    data class Offers(
        var recipes: MutableList<Recipe>? = null
    ) {
        data class Recipe(
            @JsonProperty("buy")
            var buy: Buy? = null,
            @JsonProperty("sell")
            var sell: Sell? = null,
            @JsonProperty("rewardExp")
            var rewardExp: Byte = 0,
            @JsonProperty("maxUses")
            var maxUses: Int = 9999999,
        ) {
            data class Buy(
                @JsonProperty("id")
                var id: String,
                var count: Int = 1
            )

            data class Sell(
                @JsonProperty("id")
                var id: String,
                var count: Int = 1,
                @JsonProperty("tag")
                var tag: Map<String, Any>? = null
            )
        }
    }
}
