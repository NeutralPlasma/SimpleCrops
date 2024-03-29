package eu.virtusdevelops.simplecrops.listeners

import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.locale.Locales
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropData
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropLocation
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropStorage
import eu.virtusdevelops.simplecrops.util.CropUtil
import eu.virtusdevelops.simplecrops.util.CropUtil.Companion.isCrop
import eu.virtusdevelops.simplecrops.util.nbtutil.NBTUtil
import eu.virtusdevelops.virtuscore.managers.FileManager
import eu.virtusdevelops.virtuscore.utils.HexUtil
import eu.virtusdevelops.virtuscore.utils.TextUtils
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class CropPlaceListener(private val cropStorage: CropStorage,private val nbt: NBTUtil, private val fileManager: FileManager,
                        private val locale: LocaleHandler) : Listener{

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent){
        val cropLocation = CropLocation(event.block.x, event.block.y, event.block.z, event.block.world.name)
        val item = event.itemInHand

        if(event.block.getRelative(BlockFace.DOWN).isCrop()) {
            val base = CropUtil.getBaseBlock(event.block)
            val cropLocation2 = CropLocation(base.x, base.y, base.z, base.world.name)
            if (cropStorage.crops[cropLocation2.toString()] != null && !event.player.hasPermission("simplecrops.admin.bypass")) {
                event.isCancelled = true
                return
            }
        }else if(nbt.nbt.getString(item, "cropID") != "NONE"){
            if(event.player.hasPermission("simplecrops.place")) {
                val id = nbt.nbt.getString(item, "cropID")
                val gain = nbt.nbt.getInt(item, "gain")
                val strength = nbt.nbt.getInt(item, "strength")
                val name = HexUtil.colorify(fileManager.getConfiguration("crops").getString("seeds.$id.name"))

                cropStorage.addCrop(CropData(name, gain, strength, event.player.uniqueId, id, 0), cropLocation)
            }else{
                event.isCancelled = true
                event.player.sendMessage(HexUtil.colorify(TextUtils.formatString(locale.getLocale(Locales.NO_PERMISSION), "{permission}:simplecrops.place")))
            }
        }
    }
}