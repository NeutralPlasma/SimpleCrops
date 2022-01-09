package eu.virtusdevelops.simplecrops.handlers.crophandler

import org.bukkit.Material


data class BlockDropData(
    val material: Material,
    var chance: Double,
    var max: Double = 0.0,
    var min: Double = 0.0
)