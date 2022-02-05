package eu.virtusdevelops.simplecrops.listeners

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.handlers.ParticleHandler
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.locale.Locales
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropLocation
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropStorage
import eu.virtusdevelops.simplecrops.util.CropUtil
import eu.virtusdevelops.simplecrops.util.CropUtil.Companion.isCrop
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
                        private val locale: LocaleHandler, private val plugin: SimpleCrops,
                        private val particles: ParticleHandler) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onCropBreak(event: BlockBreakEvent){

        if(event.block.isCrop()) {
            val base = CropUtil.getBaseBlock(event.block)
            val cropLocation = CropLocation(base.x, base.y, base.z, base.world.name)
            if (cropStorage.crops.containsKey(cropLocation.toString())) {

                val cropData = cropStorage.crops[cropLocation.toString()] ?: return

                val block = event.block
                if (event.player.hasPermission("simplecrops.break")) {
                    cropDrops.handleCrop(cropData, block, base, plugin.config.getBoolean("system.duplication"))
                    particles.playBreakParticles(event.player, block.location)
                } else {
                    event.player.sendMessage(TextUtils.colorFormat(TextUtils.formatString(locale.getLocale(Locales.NO_PERMISSION), "{permission}:simplecrops.break")))
                    particles.playBreakParticles(event.player, block.location)
                    event.isCancelled = true
                }
            }
        }else if(event.block.getRelative(BlockFace.UP).isCrop()){
            val base = event.block.getRelative(BlockFace.UP)
            val cropLocation = CropLocation(base.x, base.y, base.z, base.world.name)
            //event.isCancelled = true
            if (cropStorage.crops.containsKey(cropLocation.toString())) {
                val cropData = cropStorage.crops[cropLocation.toString()] ?: return

                if (event.player.hasPermission("simplecrops.break")) {
                    cropDrops.handleCrop(cropData, base, base, plugin.config.getBoolean("system.duplication"))
                    particles.playBreakParticles(event.player, base.location)

                } else {
                    event.player.sendMessage(TextUtils.colorFormat(TextUtils.formatString(locale.getLocale(Locales.NO_PERMISSION), "{permission}:simplecrops.break")))
                    particles.playBreakParticles(event.player, base.location)
                }

            }
        }else if(event.block.type == Material.GRASS){
            if( (0..100).random() > 100.0 - plugin.config.getDouble("system.dropChance")){
                cropDrops.dropRandomCrop(event.block.location)
            }
        }

        val cropLocation = CropLocation(event.block.x, event.block.y, event.block.z, event.block.world.name)
        if(cropStorage.baseBlocks.contains(cropLocation.toString())){
            val dataBlock = cropStorage.baseBlocks[cropLocation.toString()]
            if(event.player.hasPermission("simplecrops.break")){
                if (dataBlock != null) {
                    cropDrops.handleBaseBlock(dataBlock, event.block, cropLocation)
                    particles.playBreakParticles(event.player, event.block.location)
                }
            } else {
                event.player.sendMessage(TextUtils.colorFormat(TextUtils.formatString(locale.getLocale(Locales.NO_PERMISSION), "{permission}:simplecrops.break")))
                particles.playBreakParticles(event.player, event.block.location)
            }

        }

    }
}