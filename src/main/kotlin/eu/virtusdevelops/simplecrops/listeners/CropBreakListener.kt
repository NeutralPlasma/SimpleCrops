package eu.virtusdevelops.simplecrops.listeners

import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.locale.Locales
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropLocation
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropStorage
import eu.virtusdevelops.simplecrops.util.CropUtil
import eu.virtusdevelops.virtuscore.managers.FileManager
import eu.virtusdevelops.virtuscore.utils.TextUtil
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class CropBreakListener(private val cropStorage : CropStorage, private val cropDrops: CropDrops,
                        private val locale: LocaleHandler) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onCropBreak(event: BlockBreakEvent){
        if(CropUtil.isCrop(event.block)) {
            val base = CropUtil.getBaseBlock(event.block)
            val cropLocation = CropLocation(base.x, base.y, base.z, base.world.name)
            if (cropStorage.crops.containsKey(cropLocation.toString())) {
                val cropData = cropStorage.crops[cropLocation.toString()]

                if (cropData != null) {
                    if (event.player.hasPermission("simplecrops.break")) {
                        cropDrops.handleCrop(cropData, event.block, base)
                    } else {
                        event.player.sendMessage(TextUtil.colorFormat(TextUtil.formatString(locale.getLocale(Locales.NO_PERMISSION), "{permission}:simplecrops.break")))
                        event.isCancelled = true
                    }
                }
            }
        }else if(CropUtil.isCrop(event.block.getRelative(BlockFace.UP))){
            val base = event.block.getRelative(BlockFace.UP)
            val cropLocation = CropLocation(base.x, base.y, base.z, base.world.name)
            //event.isCancelled = true
            if (cropStorage.crops.containsKey(cropLocation.toString())) {
                val cropData = cropStorage.crops[cropLocation.toString()]

                if (cropData != null) {
                    if (event.player.hasPermission("simplecrops.break")) {
                        cropDrops.handleCrop(cropData, base, base)
                    } else {
                        event.player.sendMessage(TextUtil.colorFormat(TextUtil.formatString(locale.getLocale(Locales.NO_PERMISSION), "{permission}:simplecrops.break")))
                    }
                }
            }
        }
    }
}