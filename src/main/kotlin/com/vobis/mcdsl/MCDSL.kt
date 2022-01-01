package com.vobis.mcdsl

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.vobis.mcdsl.entity.Entity
import com.vobis.mcdsl.nbt.NBTBase
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.math.absoluteValue

class MCDSL(
    val namespace: String,
    val exec: MCDSL.() -> Unit
) {

    companion object {
        val DEFAULT = "default"
    }

    private val objectMapper = jacksonObjectMapper()
    val contexts = mutableMapOf<String, MutableList<String>>()

    var currentContext = "default"
    var contextPrefix = ""

    init {
        contexts[DEFAULT] = mutableListOf()
        objectMapper.propertyNamingStrategy = PropertyNamingStrategy.UPPER_CAMEL_CASE
    }

    fun withPrefix(prefix: String, exec: () -> Unit) {
        contextPrefix += prefix
        exec.invoke()
        contextPrefix = contextPrefix.replace(prefix, "")
    }

    /**
     * Inserts the given command into the context.
     * */
    fun insert(command: String, context: String = currentContext) {
        contexts.getOrPut(context, ::mutableListOf).add(contextPrefix + command)
    }

    /**
     * Inserts the given command at the start of the context.
     * */
    fun insertStart(command: String, context: String = currentContext) {
        contexts.getOrPut(context, ::mutableListOf).add(0, contextPrefix + command)
    }

    /**
     * Creates a new variable
     * */
    fun let(name: String = Util.uniqueString()): ScoreVar {
        return ScoreVar(name)
    }

    fun get(name: String): ScoreVar {
        return ScoreVar(name)
    }

    fun const(name: String): ScoreVar {
        return ScoreVar(name)
    }

    fun allPlayers(builder: (PlayersSelector.() -> Unit)? = null): Players {
        val playersSelector = if (builder != null) {
            val selector = PlayersSelector()
            builder.invoke(selector)
            selector
        } else null

        return Players(playersSelector)
    }

    fun summon(entity: Entity<*>, x: String, y: String, z: String) {
        val nbtString = objectMapper.writeValueAsString(entity.nbt)
        insert("summon ${entity.name} $x $y $z $nbtString")
    }

    fun function(name: String = Util.uniqueString(), onCall: () -> Unit): MCFunction {
        currentContext = name
        onCall.invoke()
        currentContext = DEFAULT
        return MCFunction(name)
    }

    fun addConst(value: Int) {
        if (!usingConsts) {
            insertStart("function $namespace:consts", "default")
            insertStart("scoreboard objectives add const dummy", "default")
            usingConsts = true
        }

        if (!consts.contains(value)) {
            consts.add(value)
            insert("scoreboard players set $value const $value", "consts")
        }
    }

    fun tick(onCall: () -> Unit) {
        function("tick", onCall)
    }

    fun invokeContext() {
        contexts.clear()
        exec.invoke(this)
    }

    operator fun String.unaryPlus() = insert(this)

    fun output(): String {
        invokeContext()
        return contexts.map {
            "#${it.key}.mcfunction\n${it.value.joinToString("\n")}"
        }.joinToString("\n\n")
    }

    fun write(directory: String, hooks: Boolean = false) {
        invokeContext()

        val dir = Paths.get(directory)
        val namespace = dir.resolve(namespace)
        val functions = namespace.resolve("functions")

        if (hooks) {
            writeHooks(dir)
        }

        functions.toFile().mkdirs()
        contexts.forEach {
            val file = functions.resolve(it.key + ".mcfunction").toFile()
            file.createNewFile()
            file.writeText(it.value.joinToString("\n"))
        }
    }

    private fun writeHooks(directory: Path) {
        val tick = "{\"replace\":false,\"values\":[\"$namespace:tick\"]}"
        val load = "{\"replace\":false,\"values\":[\"$namespace:default\"]}"

        val minecraft = directory.resolve("minecraft/tags").toFile()
        minecraft.mkdirs()
        minecraft.resolve("load.json").writeText(load)
        minecraft.resolve("tick.json").writeText(tick)
    }

    inner class MCFunction(val name: String) {
        fun call() {
            insert("function $namespace:$name")
        }
    }

    inner class BossBar(
        var id: String,
        var name: String
    ) {
        init {
            insert("bossbar add $namespace:$id \"$name\"")
        }

        fun setMax(max: Int) {
            insert("bossbar set $namespace:$id max $max")
        }

        fun setValue(value: ScoreVar) {
            insert("execute store result bossbar $namespace:$id value run scoreboard players get ${value.name} var")
        }

        fun setPlayers(players: Players) {
            insert("bossbar set $namespace:$id players $players")
        }

        fun setColour(colour: String) {
            insert("bossbar set $namespace:$id color $colour")
        }
    }

    var usingVar = false
    var usingConsts = false

    val consts = mutableListOf<Int>()

    inner class ScoreVar(
        val name: String,
    ) {
        init {
            if (!usingVar) {
                usingVar = true
                insertStart("scoreboard objectives add var dummy", "default")
            }
        }

        fun set(value: Int) {
            insert("scoreboard players set $name var $value")
        }

        fun set(other: ScoreVar) {
            insert("scoreboard players operation $name var = ${other.name} var")
        }

        fun add(value: Int) {
            val operation = if (value < 0) "remove" else "add"
            insert("scoreboard players $operation $name var ${value.absoluteValue}")
        }

        fun doIf(conditionFun: (ScoreVar) -> Condition, exec: () -> Unit) {
            val condition = conditionFun.invoke(this)
            withPrefix("execute if score $condition run ", exec)
        }

        inner class Condition(
            val left: Any,
            val boolOperation: BoolOperation,
            val right: Any,
        ) {
            init {
                if (right is Int) {
                    addConst(right)
                }
            }

            override fun toString(): String {
                return if (right is Int) {
                    "$left $boolOperation $right const"
                } else {
                    "$left $boolOperation $right"
                }
            }

            infix fun and(condition: Condition): Condition {
                return Condition(this, BoolOperation.EQ, condition)
            }
        }

        infix fun lt(value: Int): Condition {
            return Condition(this, BoolOperation.LT, value)
        }

        infix fun gt(value: Int): Condition {
            return Condition(this, BoolOperation.GT, value)
        }

        infix fun eq(value: Int): Condition {
            return Condition(this, BoolOperation.EQ, value)
        }

        infix fun lte(value: Int): Condition {
            return Condition(this, BoolOperation.LTE, value)
        }

        infix fun gte(value: Int): Condition {
            return Condition(this, BoolOperation.GTE, value)
        }

        infix fun matches(range: IntRange): Condition {
            return Condition(this, BoolOperation.MATCHES, range)
        }

        operator fun timesAssign(i: Int) {
            addConst(i)
            insert("scoreboard players operation $name var *= $i const")
        }

        fun min(min: Int): ScoreVar {
            addConst(min)
            insert("scoreboard players operation $name var > $min const")
            return this
        }

        fun max(max: Int): ScoreVar {
            addConst(max)
            insert("scoreboard players operation $name var < $max const")
            return this
        }

        override fun toString(): String {
            return "$name var"
        }
    }

    inner class Players(
        val selectors: PlayersSelector?
    ) {
        var at: Boolean = false
        val team = Team()

        fun spawnpoint(x: Int, y: Int, z: Int) {
            insert("spawnpoint $this $x $y $z")
        }

        fun tp(x: Int, y: Int, z: Int) {
            insert("tp $this $x $y $z")
        }

        fun clear() {
            insert("clear $this")
        }

        override fun toString(): String {
            return "@a$selectors"
        }

        fun forEach(exec: () -> Unit): Players {
            val atString = if (at) " at @s" else ""
            withPrefix("execute as $this$atString run ", exec)
            return this
        }

        fun at(): Players {
            at = true
            return this
        }

        fun give(item: String, amount: Int? = null) {
            if (amount == null) {
                insert("give $this $item")
            } else {
                insert("give $this $item $amount")
            }
        }

        inner class Team {
            fun join(team: String) {
                insert("team join $team ${this@Players}")
            }
        }
    }
}

enum class BoolOperation(val command: String) {
    LT("<"), GT(">"), GTE(">="), LTE("<="), EQ("="),
    MATCHES("matches");

    override fun toString() = command
}

class Distance(
    val range: IntRange
) {
    override fun toString(): String {
        return range.toString()
    }
}

class PlayersSelector {
    var team: String? = null
    var distance: IntRange? = null

    override fun toString(): String {
        val criteria = mutableListOf<String>()

        if (team != null) {
            criteria.add("team=$team")
        }

        if (distance != null) {
            criteria.add("distance=$distance")
        }

        return "[${criteria.joinToString(",")}]"
    }
}

private operator fun String.not() = "!$this"