package com.vobis.mcdsl.examples

import com.vobis.mcdsl.MCDSL
import com.vobis.mcdsl.entity.Villager
import com.vobis.mcdsl.nbt.NBTVillager
import com.vobis.mcdsl.nbt.NBTVillager.Offers
import com.vobis.mcdsl.nbt.NBTVillager.Offers.Recipe
import com.vobis.mcdsl.nbt.NBTVillager.Offers.Recipe.Buy
import com.vobis.mcdsl.nbt.NBTVillager.Offers.Recipe.Sell

fun main() {
    val dsl = MCDSL("summontrader") {
        function("summonTrader") {
            val villager = Villager(
                NBTVillager(
                    persistenceRequired = true,
                    silent = true,
                    noAI = true,
                    invulnerable = true,
                    villagerData = NBTVillager.VillagerData(
                        profession = "cleric",
                        level = 99,
                        type = "plains",
                    ),
                    offers = Offers(
                        recipes = mutableListOf(
                            Recipe(
                                buy = Buy(
                                    id = "emerald",
                                    count = 10
                                ),

                                sell = Sell(
                                    id = "skeleton_skull",
                                    count = 1,
                                    tag = mapOf(
                                        "spawn" to "defenceSkeleton",
                                        "team" to "red"
                                    )
                                )
                            )
                        )
                    )
                )
            )

            summon(villager, "~", "~2", "~")
        }
    }

    println(dsl.output())
}