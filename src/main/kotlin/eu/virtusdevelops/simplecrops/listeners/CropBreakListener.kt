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
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import java.util.logging.Level
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class CropBreakListener(private val cropStorage : CropStorage, private val cropDrops: CropDrops,
                        private val locale: LocaleHandler, private val plugin: SimpleCrops,
                        private val particles: ParticleHandler) : Listener {


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onCropBreakNew(event: BlockBreakEvent){
        var block = event.block
        if(!block.isCrop()) {
            block = block.getRelative(BlockFace.UP)
            if (!block.isCrop())
                if(handleBaseBlock(event.block, event.player)){
                    event.block.type = Material.AIR
                    event.isCancelled = true
                    return
                }
        }


        val base = CropUtil.getBaseBlock(block)
        val cropLocation = CropLocation(base.x, base.y, base.z, base.world.name)
        val cropData = cropStorage.crops[cropLocation.toString()]
        val player = event.player



        if (cropData != null) {

            if (!event.player.hasPermission("simplecrops.break")) {
                event.player.sendMessage(
                    TextUtils.colorFormat(
                        TextUtils.formatString(
                            locale.getLocale(Locales.NO_PERMISSION),
                            "{permission}:simplecrops.break"
                        )
                    )
                );
                particles.playBreakParticles(player, base.location)
                return
            }



            cropDrops.handleCrop(cropData, block, base, plugin.config.getBoolean("system.duplication"), player)
            particles.playBreakParticles(player, base.location)
            return
        }

//        val baseBlockData = cropStorage.baseBlocks[cropLocation.toString()]
//        if (baseBlockData != null) {
//            if (!event.player.hasPermission("simplecrops.break")) {
//                event.player.sendMessage(
//                    TextUtils.colorFormat(
//                        TextUtils.formatString(
//                            locale.getLocale(Locales.NO_PERMISSION),
//                            "{permission}:simplecrops.break"
//                        )
//                    )
//                );
//                particles.playBreakParticles(event.player, base.location)
//                return
//            }
//            cropDrops.handleBaseBlock(baseBlockData, event.block, cropLocation, player)
//            particles.playBreakParticles(player, event.block.location)
//            return
//        }



    }


    private fun handleBaseBlock(block: Block, player: Player): Boolean{
        val cropLocation = CropLocation(block.x, block.y, block.z, block.world.name)

        val baseBlockData = cropStorage.baseBlocks[cropLocation.toString()]
        if(baseBlockData != null){
            if (!player.hasPermission("simplecrops.break")){
                player.sendMessage(TextUtils.colorFormat(TextUtils.formatString(locale.getLocale(Locales.NO_PERMISSION), "{permission}:simplecrops.break")));
                particles.playBreakParticles(player, block.location)
                return true
            }
            cropDrops.handleBaseBlock(baseBlockData, block, cropLocation, player)
            particles.playBreakParticles(player, block.location)
            return true

        }

        return false
    }
}