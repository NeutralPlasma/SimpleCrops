package eu.virtusdevelops.simplecrops.util.nbtutil

import org.bukkit.inventory.ItemStack

interface NBTVer {
    fun getInt(item: ItemStack, container: String): Int{
        return 0
    }
    fun getString(item: ItemStack, container: String): String{
        return "NONE"
    }
    fun setInt(item: ItemStack, container: String, value: Int): ItemStack{
        return item
    }
    fun setString(item: ItemStack, container: String, value: String): ItemStack{
        return item
    }
    fun hasValueString(item: ItemStack, container: String): Boolean{
        return false
    }
    fun hasValueInt(item: ItemStack, container: String): Boolean{
        return false
    }
}