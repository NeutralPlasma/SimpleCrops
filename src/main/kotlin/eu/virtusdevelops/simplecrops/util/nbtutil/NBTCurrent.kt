package eu.virtusdevelops.simplecrops.util.nbtutil

import eu.virtusdevelops.simplecrops.SimpleCrops
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

class NBTCurrent(plugin: SimpleCrops) : NBTVer{
    private val plugin = plugin

    override fun getInt(item: ItemStack, container: String): Int {
        val key = NamespacedKey(plugin, container)
        val meta:ItemMeta? = item.itemMeta
        if(meta != null) {
            val tagContainer: PersistentDataContainer = meta.persistentDataContainer
            if(tagContainer.has(key, PersistentDataType.INTEGER)){
                val value = tagContainer.getOrDefault(key, PersistentDataType.INTEGER, 0)
                return value
            }
        }
        return super.getInt(item, container)
    }

    override fun getString(item: ItemStack, container: String): String {
        val key = NamespacedKey(plugin, container)
        val meta:ItemMeta? = item.itemMeta
        if(meta != null) {
            val tagContainer: PersistentDataContainer = meta.persistentDataContainer
            if(tagContainer.has(key, PersistentDataType.STRING)){
                val value = tagContainer.getOrDefault(key, PersistentDataType.STRING, "NONE")
                return value
            }
        }
        return super.getString(item, container)
    }

    override fun setInt(item: ItemStack, container: String, value: Int): ItemStack {
        val key = NamespacedKey(plugin, container)
        try{
            val meta:ItemMeta? = item.itemMeta
            if(meta != null) {
                val tagContainer: PersistentDataContainer = meta.persistentDataContainer
                tagContainer.set(key, PersistentDataType.INTEGER, value)
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
            val meta = item.itemMeta
            if(meta != null) {
                val tagContainer: PersistentDataContainer = meta.persistentDataContainer
                tagContainer.set(key, PersistentDataType.STRING, value)
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
                val tagContainer: PersistentDataContainer = meta.persistentDataContainer
                return tagContainer.has(key, PersistentDataType.STRING)
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
                val tagContainer: PersistentDataContainer = meta.persistentDataContainer
                return tagContainer.has(key, PersistentDataType.INTEGER)
            }
        }catch (exception: NullPointerException){
            return false
        }

        return false
    }
}