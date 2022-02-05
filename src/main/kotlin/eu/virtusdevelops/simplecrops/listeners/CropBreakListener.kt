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
    fun onCropBreakNew(event: BlockBreakEvent){
        var block = event.block
        if(!block.isCrop()) {
            block = block.getRelative(BlockFace.UP)
            if (!block.isCrop()) return
        }
        val base = CropUtil.getBaseBlock(block)
        val cropLocation = CropLocation(base.x, base.y, base.z, base.world.name)
        val cropData = cropStorage.crops[cropLocation.toString()]




        if(cropData != null) {
            if (!event.player.hasPermission("simplecrops.break")){
                event.player.sendMessage(TextUtils.colorFormat(TextUtils.formatString(locale.getLocale(Locales.NO_PERMISSION), "{permission}:simplecrops.break")));
                particles.playBreakParticles(event.player, base.location)
                return
            }
            cropDrops.handleCrop(cropData, block, base, plugin.config.getBoolean("system.duplication"))
            particles.playBreakParticles(event.player, base.location)
        }

        val baseBlockData = cropStorage.baseBlocks[cropLocation.toString()]
        if(baseBlockData != null){
            if (!event.player.hasPermission("simplecrops.break")){
                event.player.sendMessage(TextUtils.colorFormat(TextUtils.formatString(locale.getLocale(Locales.NO_PERMISSION), "{permission}:simplecrops.break")));
                particles.playBreakParticles(event.player, base.location)
                return
            }
            cropDrops.handleBaseBlock(baseBlockData, event.block, cropLocation)
            particles.playBreakParticles(event.player, event.block.location)
            return

        }
    }
}