package eu.virtusdevelops.simplecrops.handlers.crophandler

import org.bukkit.Material


data class BlockDropData(
    val material: Material,
    var chance: Double
)