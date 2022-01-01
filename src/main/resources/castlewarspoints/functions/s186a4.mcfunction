execute as @a[team=red,distance=0..15] run scoreboard players add s1869f var 1
execute as @a[team=blue,distance=0..15] run scoreboard players remove s1869f var 1
execute store result bossbar pointa value run scoreboard players get s1869f var
execute if score s1869f var = 0 const run bossbar set pointa color white
execute if score s1869f var < 0 const run bossbar set pointa color blue
execute if score s1869f var > 0 const run bossbar set pointa color red
execute as @a[team=red,distance=0..15] run scoreboard players add s186a0 var 1
execute as @a[team=blue,distance=0..15] run scoreboard players remove s186a0 var 1
execute store result bossbar pointb value run scoreboard players get s186a0 var
execute if score s186a0 var = 0 const run bossbar set pointb color white
execute if score s186a0 var < 0 const run bossbar set pointb color blue
execute if score s186a0 var > 0 const run bossbar set pointb color red
execute as @a[team=red,distance=0..15] run scoreboard players add s186a1 var 1
execute as @a[team=blue,distance=0..15] run scoreboard players remove s186a1 var 1
execute store result bossbar pointc value run scoreboard players get s186a1 var
execute if score s186a1 var = 0 const run bossbar set pointc color white
execute if score s186a1 var < 0 const run bossbar set pointc color blue
execute if score s186a1 var > 0 const run bossbar set pointc color red
execute as @a[team=red,distance=0..15] run scoreboard players add s186a2 var 1
execute as @a[team=blue,distance=0..15] run scoreboard players remove s186a2 var 1
execute store result bossbar pointd value run scoreboard players get s186a2 var
execute if score s186a2 var = 0 const run bossbar set pointd color white
execute if score s186a2 var < 0 const run bossbar set pointd color blue
execute if score s186a2 var > 0 const run bossbar set pointd color red
execute as @a[team=red,distance=0..15] run scoreboard players add s186a3 var 1
execute as @a[team=blue,distance=0..15] run scoreboard players remove s186a3 var 1
execute store result bossbar pointe value run scoreboard players get s186a3 var
execute if score s186a3 var = 0 const run bossbar set pointe color white
execute if score s186a3 var < 0 const run bossbar set pointe color blue
execute if score s186a3 var > 0 const run bossbar set pointe color red