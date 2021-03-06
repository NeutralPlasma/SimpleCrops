package eu.virtusdevelops.simplecrops.listeners

import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.locale.Locales
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropLocation
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropStorage
import eu.virtusdevelops.simplecrops.util.CropUtil
import eu.virtusdevelops.virtuscore.utils.TextUtils
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
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

                val cropData = cropStorage.crops[cropLocation.toString()] ?: return

                val block = event.block
                if (event.player.hasPermission("simplecrops.break")) {
                    cropDrops.handleCrop(cropData, block, base)
                    event.player.spawnParticle(Particle.CLOUD, block.x.toDouble() + 0.5, block.y.toDouble() + 0.2, block.z.toDouble()+0.5, 5, 0.01, 0.0, 0.01, 0.02)
                } else {
                    event.player.sendMessage(TextUtils.colorFormat(TextUtils.formatString(locale.getLocale(Locales.NO_PERMISSION), "{permission}:simplecrops.break")))
                    event.player.spawnParticle(Particle.REDSTONE, block.x.toDouble() + 0.5, block.y.toDouble() + 0.2, block.z.toDouble() + 0.5, 10, 0.01, 0.0, 0.01, 0.02, Particle.DustOptions(Color.RED, 1.5F))
                    event.isCancelled = true
                }
            }
        }else if(CropUtil.isCrop(event.block.getRelative(BlockFace.UP))){
            val base = event.block.getRelative(BlockFace.UP)
            val cropLocation = CropLocation(base.x, base.y, base.z, base.world.name)
            //event.isCancelled = true
            if (cropStorage.crops.containsKey(cropLocation.toString())) {
                val cropData = cropStorage.crops[cropLocation.toString()] ?: return

                if (event.player.hasPermission("simplecrops.break")) {
                    cropDrops.handleCrop(cropData, base, base)
                    event.player.spawnParticle(Particle.CLOUD, base.x.toDouble() + 0.5, base.y.toDouble() + 0.5, base.z.toDouble(), 5, 0.01, 0.0, 0.01, 0.02)
                } else {
                    event.player.sendMessage(TextUtils.colorFormat(TextUtils.formatString(locale.getLocale(Locales.NO_PERMISSION), "{permission}:simplecrops.break")))
                    event.player.spawnParticle(Particle.REDSTONE, base.x.toDouble() + 0.5, base.y.toDouble() + 0.2, base.z.toDouble() + 0.5, 10, 0.01, 0.0, 0.01, 0.02, Particle.DustOptions(Color.RED, 1.5F))
                }

            }
        }else if(event.block.type == Material.GRASS){
            if( (0..100).random() > 60){
                cropDrops.dropRandomCrop(event.block.location)
            }
        }
    }
}