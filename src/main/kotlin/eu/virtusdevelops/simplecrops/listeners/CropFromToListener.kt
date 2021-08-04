package eu.virtusdevelops.simplecrops.listeners

import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropLocation
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropStorage
import eu.virtusdevelops.simplecrops.util.CropUtil
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockFromToEvent

class CropFromToListener(private val cropStorage: CropStorage, private val cropDrops: CropDrops) : Listener {


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onFromTo(event: BlockFromToEvent){
        val block = event.toBlock
        if(CropUtil.isCrop(block)){
            if(CropUtil.isMultiBlock(block)){
                val base = CropUtil.getBaseBlock(block)
                val location = CropLocation(base.x, base.y, base.z, base.world.name)
                val crop = cropStorage.crops[location.toString()]

                if(crop != null){
                    cropDrops.handleCrop(crop, block, base)
                    event.isCancelled = true
                }
            }else{
                val location = CropLocation(block.x, block.y, block.z, block.world.name)
                val crop = cropStorage.crops[location.toString()]

                if(crop != null){
                    event.isCancelled = true
                    block.type = Material.AIR
                    cropDrops.dropSeed(crop, block.location, false)
                    if(CropUtil.isFullyGrown(block)){
                        cropDrops.dropDrops(block, crop, Bukkit.getPlayer(crop.placedBy))
                    }
                    cropStorage.removeCrop(location)
                }
            }
        }
    }
}
