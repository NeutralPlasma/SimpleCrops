package eu.virtusdevelops.simplecrops.handlers.crophandler

import org.bukkit.inventory.ItemStack

data class DropData(
        val item: ItemStack,
        var min: Int,
        var max: Int
)