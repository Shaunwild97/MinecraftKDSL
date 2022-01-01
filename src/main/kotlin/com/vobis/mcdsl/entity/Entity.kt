package com.vobis.mcdsl.entity

import com.vobis.mcdsl.nbt.NBTBase

abstract class Entity<T : NBTBase>(
    val name: String,
    val nbt: T? = null
)
