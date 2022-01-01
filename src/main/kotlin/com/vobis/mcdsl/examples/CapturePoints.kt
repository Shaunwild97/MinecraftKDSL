package com.vobis.mcdsl

data class CapturePoint(
    val bossBar: MCDSL.BossBar,
    val capture: MCDSL.ScoreVar,
    val absoluteCapture: MCDSL.ScoreVar,
)

fun main() {
    val dsl = MCDSL("castlewarspoints") {
        val bars = listOf("Pyramid", "Volcano", "Bridge", "Castle", "Forest")
            .map {
                val bossBar = BossBar("point$it".lowercase(), it)
                bossBar.setMax(500)
                CapturePoint(bossBar, let(), let())
            }

        function("start") {
            bars.forEach {
                it.capture.set(0)
            }
        }

        val gameTick10s = get("gameTick10s")

        bars.forEach {
            function(it.bossBar.id) {
                allPlayers { team = "red"; distance = 0..15 }.at().forEach { it.capture.add(1) }
                allPlayers { team = "blue"; distance = 0..15 }.at().forEach { it.capture.add(-1) }

                it.capture.min(-500).max(500)

                it.bossBar.setPlayers(allPlayers { distance = 0..15 })

                it.absoluteCapture.set(it.capture)

                it.absoluteCapture.doIf({ it lt 0 }) {
                    it.absoluteCapture *= -1
                }

                it.capture.doIf({ it eq 0 }) {
                    +"setblock ~ ~5 ~ white_wool"
                    +"setblock ~ ~6 ~ white_wool"
                }

                gameTick10s.doIf({ it eq 0 }) {
                    it.capture.doIf({ (it eq -500) }) {
                        +"scoreboard players add blue points 1"
                        allPlayers{team="blue"}.give("emerald")
                    }

                    it.capture.doIf({ (it eq 500) }) {
                        +"scoreboard players add red points 1"
                        allPlayers{team="red"}.give("emerald")
                    }
                }

                it.capture.doIf({ it eq -500 }) {
                    +"setblock ~ ~5 ~ blue_wool"
                    +"setblock ~ ~6 ~ blue_wool"
                }

                it.capture.doIf({ it eq 500 }) {
                    +"setblock ~ ~5 ~ red_wool"
                    +"setblock ~ ~6 ~ red_wool"
                }

                it.capture.doIf({ it matches 1..500 }) {
                    +"particle dust_color_transition 1.0 0.0 0.0 1.0 1.0 0.0 0.0 ~ ~40 ~ 1 5 1 10 50 force"
                }

                it.capture.doIf({ it matches -500..-1 }) {
                    +"particle dust_color_transition 0.0 0.0 0.0 1.0 0.0 0.0 1.0 ~ ~40 ~ 1 5 1 10 50 force"
                }

                it.capture.doIf({ it matches -499..499 }) {
                    +"particle dust_color_transition 1.0 1.0 1.0 1.0 1.0 1.0 1.0 ~ ~40 ~ 1 5 1 10 50 force"
                }

                it.bossBar.setValue(it.absoluteCapture)

                it.capture.doIf({ it eq 0 }) {
                    it.bossBar.setColour("white")
                }

                it.capture.doIf({ it lt 0 }) {
                    it.bossBar.setColour("blue")
                }

                it.capture.doIf({ it gt 0 }) {
                    it.bossBar.setColour("red")
                }
            }
        }
    }

//    println(dsl.output())
    dsl.write("C:\\Users\\Shaun\\Documents\\Minecraft Servers\\SpigotServer\\world\\datapacks\\castlewars\\data")
}
