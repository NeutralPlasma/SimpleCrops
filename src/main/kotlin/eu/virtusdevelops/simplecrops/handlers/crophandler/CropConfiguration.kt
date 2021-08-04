package eu.virtusdevelops.simplecrops.handlers.crophandler

import org.bukkit.util.Vector

data class CropConfiguration(
        var itemDrops: MutableList<DropData>,
        var blockDrops: MutableList<BlockDropData>,
        var commandDrops: MutableList<String>,
        var name: String,
        var minGain: Int,
        var maxGain: Int,
        var minStrength: Int,
        var maxStrength: Int,
        var useBoneMeal: Boolean,
        var boneMeal: Int,
        var dropNaturally: Boolean,
        var dropChance: Double,
        var duplicateChance: Double,
        var duplicate: Boolean,
        var structureName: String?
)