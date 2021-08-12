
package eu.virtusdevelops.simplecrops.util.nbtutil

import eu.virtusdevelops.simplecrops.SimpleCrops
import org.bukkit.NamespacedKey
import org.bukkit.Warning
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.tags.CustomItemTagContainer
import org.bukkit.inventory.meta.tags.ItemTagType

@Suppress("OverridingDeprecatedMember", "DEPRECATION")
class NBTLegacy(plugin: SimpleCrops) : NBTVer{
    private val plugin = plugin

    override fun getInt(item: ItemStack, container: String): Int {
        val key = NamespacedKey(plugin, container)
        val meta: ItemMeta? = item.itemMeta
        if(meta != null) {
            val tagContainer: CustomItemTagContainer = meta.customTagContainer
            if(tagContainer.hasCustomTag(key, ItemTagType.INTEGER)){
                val value = tagContainer.getCustomTag(key, ItemTagType.INTEGER)
                if(value != null) {
                    return value
                }
            }
        }
        return super.getInt(item, container)
    }

    override fun getString(item: ItemStack, container: String): String {
        val key = NamespacedKey(plugin, container)
        val meta: ItemMeta? = item.itemMeta
        if(meta != null) {
            val tagContainer: CustomItemTagContainer = meta.customTagContainer
            if(tagContainer.hasCustomTag(key, ItemTagType.STRING)){
                val value = tagContainer.getCustomTag(key, ItemTagType.STRING)
                if(value != null) {
                    return value
                }
            }
        }
        return super.getString(item, container)
    }

    override fun setInt(item: ItemStack, container: String, value: Int): ItemStack {
        val key = NamespacedKey(plugin, container)
        try{
            val meta: ItemMeta? = item.itemMeta
            if(meta != null) {
                val tagContainer: CustomItemTagContainer = meta.customTagContainer
                tagContainer.setCustomTag(key, ItemTagType.INTEGER, value)
                item.itemMeta = meta
                return item
            }
        }catch (exception: NullPointerException){
            exception.cause;
            exception.fillInStackTrace()
        }
        return item
    }

    override fun setString(item: ItemStack, container: String, value: String): ItemStack {
        val key = NamespacedKey(plugin, container)
        try{
            val meta: ItemMeta? = item.itemMeta
            if(meta != null) {
                val tagContainer: CustomItemTagContainer = meta.customTagContainer
                tagContainer.setCustomTag(key, ItemTagType.STRING, value)
                item.itemMeta = meta
                return item
            }
        }catch (exception: NullPointerException){
            exception.cause;
            exception.fillInStackTrace()
        }
        return item
    }

    override fun hasValueString(item: ItemStack, container: String): Boolean {
        val key = NamespacedKey(plugin, container)
        try{
            val meta = item.itemMeta
            if(meta != null) {
                val tagContainer: CustomItemTagContainer = meta.customTagContainer
                return tagContainer.hasCustomTag(key, ItemTagType.STRING)
            }
        }catch (exception: NullPointerException){
            return false
        }

        return false
    }

    override fun hasValueInt(item: ItemStack, container: String): Boolean {
        val key = NamespacedKey(plugin, container)
        try{
            val meta = item.itemMeta
            if(meta != null) {
                val tagContainer: CustomItemTagContainer = meta.customTagContainer
                return tagContainer.hasCustomTag(key, ItemTagType.INTEGER)
            }
        }catch (exception: NullPointerException){
            return false
        }

        return false
    }
}