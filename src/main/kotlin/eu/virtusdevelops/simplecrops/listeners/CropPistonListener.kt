package eu.virtusdevelops.simplecrops.listeners

import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropLocation
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropStorage
import eu.virtusdevelops.simplecrops.util.CropUtil
import eu.virtusdevelops.simplecrops.util.CropUtil.Companion.isCrop
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent

class CropPistonListener(private val cropStorage: CropStorage, private val cropDrops: CropDrops) : Listener{

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPistonExtend(event: BlockPistonExtendEvent){
        val direction = event.direction
        val blocks = event.blocks

        for (block in blocks){
            if(block.isCrop()){
                val base = CropUtil.getBaseBlock(block)
                val location = CropLocation(base.x, base.y, base.z, base.world.name)
                val crop = cropStorage.crops[location.toString()]
                if(crop != null){
                    event.isCancelled = true
                    return
                }
            }else if(block.getRelative(BlockFace.UP).isCrop()){
                val base = CropUtil.getBaseBlock(block.getRelative(BlockFace.UP))
                val location = CropLocation(base.x, base.y, base.z, base.world.name)
                val crop = cropStorage.crops[location.toString()]
                if(crop != null){
                    event.isCancelled = true
                    return
                }
            }else if(block.getRelative(direction).isCrop()){
                val base = CropUtil.getBaseBlock(block.getRelative(direction))
                val location = CropLocation(base.x, base.y, base.z, base.world.name)
                val crop = cropStorage.crops[location.toString()]
                if(crop != null){
                    event.isCancelled = true
                    return
                }
            }

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPistonRetract(event: BlockPistonRetractEvent){
        val direction = event.direction
        val blocks = event.blocks

        for (block in blocks){
            if(block.isCrop()){
                val base = CropUtil.getBaseBlock(block)
                val location = CropLocation(base.x, base.y, base.z, base.world.name)
                val crop = cropStorage.crops[location.toString()]
                if(crop != null){
                    event.isCancelled = true
                    return
                }
            }else if(block.getRelative(BlockFace.UP).isCrop()){
                val base = CropUtil.getBaseBlock(block.getRelative(BlockFace.UP))
                val location = CropLocation(base.x, base.y, base.z, base.world.name)
                val crop = cropStorage.crops[location.toString()]
                if(crop != null){
                    event.isCancelled = true
                    return
                }
            }else if(block.getRelative(direction).isCrop()){
                val base = CropUtil.getBaseBlock(block.getRelative(direction))
                val location = CropLocation(base.x, base.y, base.z, base.world.name)
                val crop = cropStorage.crops[location.toString()]
                if(crop != null){
                    event.isCancelled = true
                    return
                }
            }

        }
    }


}