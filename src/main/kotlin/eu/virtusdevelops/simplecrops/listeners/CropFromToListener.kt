package eu.virtusdevelops.simplecrops.listeners

import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropData
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropLocation
import eu.virtusdevelops.simplecrops.storage.cropstorage.CropStorage
import eu.virtusdevelops.simplecrops.util.CropUtil
import eu.virtusdevelops.simplecrops.util.CropUtil.Companion.isCrop
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockFromToEvent

class CropFromToListener(private val cropStorage: CropStorage, private val cropDrops: CropDrops) : Listener {

    private var crops = mutableMapOf<Block, CropData>();


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onFromTo(event: BlockFromToEvent){
        val block = event.toBlock

        if(!block.isCrop()) return

        if(crops.keys.contains(block)){


            // drop crop drops

            block.type = Material.AIR
            crops[block]?.let { cropDrops.dropDrops(block, it, null) }
            crops.remove(block)

        }


        if(CropUtil.isMultiBlock(block)){
            val base = CropUtil.getBaseBlock(block)
            val location = CropLocation(base.x, base.y, base.z, base.world.name)
            val crop = cropStorage.crops[location.toString()] ?: return

//            cropDrops.handleCrop(crop, block, base)
            event.isCancelled = true

        }else{
            val location = CropLocation(block.x, block.y, block.z, block.world.name)
            val crop = cropStorage.crops[location.toString()] ?: return

            event.isCancelled = true
            //block.type = Material.AIR
//            cropDrops.dropSeed(crop, block.location, false)
//            if(CropUtil.isFullyGrown(block)){
//                cropDrops.dropDrops(block, crop, Bukkit.getPlayer(crop.placedBy))
//            }
//            cropStorage.removeCrop(location)

        }

    }
}
