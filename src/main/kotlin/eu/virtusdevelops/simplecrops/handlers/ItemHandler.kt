package eu.virtusdevelops.simplecrops.handlers

import eu.virtusdevelops.virtuscore.managers.FileManager
import eu.virtusdevelops.virtuscore.utils.HexUtil
import eu.virtusdevelops.virtuscore.utils.TextUtils
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemHandler (private val fileManager: FileManager){


    /**
     * Gets custom item from the items.yml configuration file.
     *
     * @itemName ID of the item in the items.yml file
     *
     * @returns Returns valid item.
     */
    fun getItem(itemName: String): ItemStack {
        val name = fileManager.getConfiguration("items").getString("items.$itemName")
        val materialName = fileManager.getConfiguration("items").getString("items.$itemName.material")
        val lore = fileManager.getConfiguration("items").getStringList("items.$itemName.lore")
        if(name != null && materialName != null) {
            val material = Material.getMaterial(materialName)
            if (material != null) {
                val item = ItemStack(material)
                val itemMeta = item.itemMeta
                if(itemMeta != null) {
                    itemMeta.lore = TextUtils.colorFormatList(lore)
                    itemMeta.setDisplayName(HexUtil.colorify(name))
                    if(fileManager.getConfiguration("items").contains("items.$itemName.custom-model-data")){
                        itemMeta.setCustomModelData(fileManager.getConfiguration("items").getInt("items.$itemName.custom-model-data"))
                    }
                    item.itemMeta = itemMeta
                }
                return item
            }
        }
        return ItemStack(Material.STONE)
    }
}