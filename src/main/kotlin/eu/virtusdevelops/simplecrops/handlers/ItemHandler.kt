package eu.virtusdevelops.simplecrops.handlers

import eu.virtusdevelops.virtuscore.managers.FileManager
import eu.virtusdevelops.virtuscore.utils.TextUtil
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemHandler (private val fileManager: FileManager){

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
                    itemMeta.lore = TextUtil.colorFormatList(lore)
                    itemMeta.setDisplayName(TextUtil.colorFormat(name))
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