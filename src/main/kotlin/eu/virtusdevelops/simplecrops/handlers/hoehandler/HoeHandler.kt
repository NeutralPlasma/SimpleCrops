package eu.virtusdevelops.simplecrops.handlers.hoehandler

import eu.virtusdevelops.simplecrops.handlers.ItemHandler
import eu.virtusdevelops.simplecrops.util.nbtutil.NBTUtil
import eu.virtusdevelops.simplecrops.util.nbtutil.NBTVer
import eu.virtusdevelops.virtuscore.VirtusCore
import eu.virtusdevelops.virtuscore.managers.FileManager
import eu.virtusdevelops.virtuscore.utils.TextUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class HoeHandler(private val fileManager: FileManager, private val itemHandler: ItemHandler, private var nbtUtil: NBTUtil) {
    val hoeConfigurations = mutableMapOf<String, HoeConfiguration>()


    init {
        cacheConfigurations()
    }


    fun cacheConfigurations(){
        hoeConfigurations.clear()

        val configurationSection = fileManager.getConfiguration("hoes").getConfigurationSection("hoes") ?: return

        for(entry in configurationSection.getKeys(false)){
            val uses = configurationSection.getInt("$entry.uses")
            val size = configurationSection.getInt("$entry.size")

            val itemData = configurationSection.getString("$entry.item")?.split(":") ?: return

            if(itemData[0].equals("ITEM", true)){
                val mat = Material.getMaterial(itemData[1])
                if(mat != null){
                    val lore = configurationSection.getStringList("$entry.lore")
                    val configuration = HoeConfiguration(mat, uses, entry, lore, size)
                    hoeConfigurations[entry] = configuration
                }
            }
        }

        Bukkit.getConsoleSender().sendMessage(hoeConfigurations.toString())
    }

    fun createHoe(id: String) : ItemStack?{
        val configuration = hoeConfigurations[id]
        if(configuration != null){
            var item = ItemStack(configuration.mat)
            val meta = item.itemMeta
            if(meta != null){
                val lore = TextUtils.formatList(configuration.lore, "{uses}:${configuration.uses}","{size}:${configuration.size}")
                meta.lore = TextUtils.colorFormatList(lore)
                item.itemMeta = meta
            }
            item = nbtUtil.nbt.setString(item, "hoeID", id)
            item = nbtUtil.nbt.setInt(item, "size", configuration.size)
            item = nbtUtil.nbt.setInt(item, "uses", configuration.uses)

            return item
        }
        return null
    }

    fun updateHoe(id: String, item: ItemStack, uses: Int, size: Int): ItemStack{
        val configuration = hoeConfigurations[id]
        if(configuration != null){
            val meta = item.itemMeta
            if(meta != null){
                var lore = configuration.lore
                lore = TextUtils.colorFormatList(TextUtils.formatList(lore, "{uses}:$uses", "{size}:$size"))
                meta.lore = lore
                item.itemMeta = meta
            }
            return nbtUtil.nbt.setInt(item, "uses", uses)
        }
        return item
    }
}