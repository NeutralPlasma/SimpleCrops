package eu.virtusdevelops.simplecrops.handlers.hoehandler

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

data class HoeConfiguration(
        val name: String,
        val mat: Material,
        val uses: Int,
        val id: String,
        val lore: MutableList<String>,
        val size: Int,
        val modelID: Int?
)